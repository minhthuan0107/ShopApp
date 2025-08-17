package com.project.shopapp.services.admin.coupon;

import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.admin.coupon.NotificationMessage;
import com.project.shopapp.dtos.admin.coupon.SendCouponDto;
import com.project.shopapp.dtos.customer.coupon.CouponDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.Notification;
import com.project.shopapp.models.User;
import com.project.shopapp.models.UserCoupon;
import com.project.shopapp.repositories.CouponRepository;
import com.project.shopapp.repositories.NotificationRepository;
import com.project.shopapp.repositories.UserCouponRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.admin.coupon.CouponResponse;
import com.project.shopapp.responses.customer.notification.NotificationResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CouponAdminService implements ICouponAdminService {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponDto couponDto) throws Exception {
        if (couponRepository.existsByCode(couponDto.getCode())) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.COUPON_CODE_ALREADY_EXISTS));
        }
        Coupon coupon = Coupon.builder()
                .type(couponDto.getType())
                .code(couponDto.getCode())
                .value(couponDto.getValue())
                .minOrderValue(couponDto.getMinOrderValue())
                .quantity(couponDto.getQuantity())
                .expiryDate(couponDto.getExpiryDate())
                .build();
        coupon.setActive(true);
        couponRepository.save(coupon);
        return CouponResponse.fromCoupon(coupon);
    }

    @Override
    public Page<CouponResponse> getAllCoupons(PageRequest pageRequest, String keyword) {
        Page<Coupon> couponPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            couponPage = couponRepository.findAll(pageRequest);
        } else {
            couponPage = couponRepository.findByCodeContainingIgnoreCase(keyword, pageRequest);
        }
        // Kiểm tra hạn và tự động tắt mã
        couponPage.forEach(coupon -> {
            if (coupon.getExpiryDate().isBefore(LocalDate.now()) && coupon.isActive()) {
                coupon.setActive(false);
                couponRepository.save(coupon);
            }
        });
        return couponPage.map(CouponResponse::fromCoupon);
    }

    @Override
    @Transactional
    public CouponResponse checkCoupon(Long userId, String code) throws Exception {
        User existUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND)));
        // Check hết hạn
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            coupon.setActive(false);
            couponRepository.save(coupon);
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_EXPIRED));
        }
        // Nếu là mã riêng => kiểm tra UserCoupon
        if (!coupon.isPublic()) {
            userCouponRepository.findByUserIdAndCouponId(userId, coupon.getId())
                    .orElseThrow(() -> new DataNotFoundException(
                            localizationUtils.getLocalizedMessage(MessageKeys.COUPON_ACCESS_DENIED)));
        }
        // Kiểm tra đã dùng chưa
        boolean alreadyUsed = userCouponRepository.existsByUserIdAndCouponIdAndStatus(
                userId, coupon.getId(), CouponStatus.USED);
        if (alreadyUsed) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_ALREADY_USED));
        }
        return CouponResponse.fromCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponResponse toggleCouponStatus(Long couponId) throws Exception {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND)));
        boolean newStatus = !coupon.isActive();
        coupon.setActive(newStatus);
        couponRepository.save(coupon);
        return CouponResponse.fromCoupon(coupon);
    }

    @Override
    @Transactional
    public void sendCouponToAllUsers(String couponCode) throws Exception{
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND)));
        coupon.setPublic(true);
        coupon.setSent(true);
        couponRepository.save(coupon);
        List<User> users = userRepository.findAll();
        // Format ngày hết hạn
        String expiredDate = coupon.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        for (User user : users){
            String content = "Bạn có mã giảm giá mới: " + coupon.getCode()
                    + ". Hạn sử dụng đến: " + expiredDate;
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle("Mã giảm giá mới!");
            notification.setContent(content);
            notification.setType("COUPON");
            notification.setIsRead(false);
            notificationRepository.save(notification);
            // Gửi qua RabbitMQ
            NotificationResponse response = NotificationResponse.fromNotification(notification);
            rabbitTemplate.convertAndSend("coupon.exchange", "coupon.notify",response);

        }
    }

    @Override
    @Transactional
    public void sendCouponToUsers(SendCouponDto sendCouponDto) throws Exception {
        Coupon coupon = couponRepository.findByCode(sendCouponDto.getCouponCode())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND)));
        coupon.setSent(true);
        couponRepository.save(coupon);
        List<User> users = userRepository.findAllById(sendCouponDto.getUserIds());
        // Format ngày hết hạn
        String expiredDate = coupon.getExpiryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        for (User user : users) {
            //Tạo bản ghi UserCoupon
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUser(user);
            userCoupon.setCoupon(coupon);
            userCoupon.setStatus(CouponStatus.ACTIVE);
            userCouponRepository.save(userCoupon);
            //Gửi thông báo
            String content = "Bạn có mã giảm giá mới: " + coupon.getCode()
                    + ". Hạn sử dụng đến: " + expiredDate;
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle("Mã giảm giá mới!");
            notification.setContent(content);
            notification.setType("COUPON");
            notification.setIsRead(false);
            notificationRepository.save(notification);
            NotificationResponse response = NotificationResponse.fromNotification(notification);
            rabbitTemplate.convertAndSend("coupon.exchange", "coupon.notify", response);
        }
    }
}
