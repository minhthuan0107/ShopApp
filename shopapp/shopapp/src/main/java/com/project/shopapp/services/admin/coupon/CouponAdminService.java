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
        return couponPage.map(CouponResponse::fromCoupon);
    }

    @Override
    @Transactional
    public CouponResponse checkCoupon(Long userId, String code) throws Exception {
        User existUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));
        Coupon coupon = couponRepository.findByCode(code).
                orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND)));
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            coupon.setActive(false);
            couponRepository.save(coupon);
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_EXPIRED));
        }
        // Kiểm tra coupon đã được dùng bởi user này chưa
        boolean alreadyUsed = userCouponRepository.existsByUserIdAndCouponIdAndStatus(
                userId, coupon.getId(), CouponStatus.USED);
        if (alreadyUsed) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_ALREADY_USED));
        }
        return CouponResponse.fromCoupon(coupon);
    }

    @Override
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
    public void sendCouponToUsers(SendCouponDto sendCouponDto) {
        List<User> users = userRepository.findAllById(sendCouponDto.getUserIds());
        for (User user : users){
            String content = "Bạn có mã giảm giá mới: " + sendCouponDto.getCouponCode();
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
}
