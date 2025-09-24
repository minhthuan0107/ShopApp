package com.project.shopapp.services.admin.coupon;

import com.project.shopapp.commons.CouponStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.admin.coupon.SendCouponDto;
import com.project.shopapp.dtos.customer.coupon.CouponDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CouponRepository;
import com.project.shopapp.repositories.NotificationRepository;
import com.project.shopapp.repositories.UserCouponRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.admin.coupon.CouponResponse;
import com.project.shopapp.responses.customer.notification.NotificationResponse;
import com.project.shopapp.ultis.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CouponAdminServiceTest {


    private PageRequest pageRequest;
    @Mock
    private LocalizationUtils localizationUtils;
    @InjectMocks
    private CouponAdminService couponAdminService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pageRequest = PageRequest.of(0, 10);
    }

    //Test tạo mới coupon
    //Case 1: Coupon đã tồn tại
    @Test
    void createCoupon_ShouldThrowException_WhenCouponAlreadyExists() throws Exception {
        CouponDto couponDto = new CouponDto("fixed",
                "MX300",
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(100), 1,
                LocalDate.parse("2025-09-08"));
        when(couponRepository.existsByCode(couponDto.getCode())).
                thenReturn(true);
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_CODE_ALREADY_EXISTS)).
                thenReturn("Mã giảm giá đã sử dụng");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.createCoupon(couponDto));
        assertEquals("Mã giảm giá đã sử dụng", exception.getMessage());
        verify(couponRepository, never()).save(any());

    }

    //Case 2: Tạo coupon thành công
    @Test
    void createCoupon_ShouldCreateNewCoupon_WhenNotExists() throws Exception {
        CouponDto couponDto = new CouponDto(
                "fixed",
                "MX300",
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(100),
                1,
                LocalDate.parse("2025-09-08"));
        when(couponRepository.existsByCode(couponDto.getCode())).thenReturn(false);
        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(i -> {
                    Coupon saved = i.getArgument(0);
                    saved.setId(10L); // giả lập DB gán id
                    return saved;
                });
        // when
        CouponResponse result = couponAdminService.createCoupon(couponDto);
        // then
        assertNotNull(result);
        assertEquals("MX300", result.getCode());
        assertEquals(10L, result.getId()); // đã được "lưu"
        assertTrue(result.isActive());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    //Test lấy danh sách coupon
    // Case 1: Keyword là null
    @Test
    void getAllCoupons_ShouldCallFindAll_WhenKeywordIsNull() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .type("fixed")
                .code("MX300")
                .minOrderValue(BigDecimal.valueOf(100.0))
                .isPublic(true)
                .isSent(false)
                .isActive(true)
                .expiryDate(LocalDate.parse("2025-09-08"))
                .quantity(1)
                .value(BigDecimal.valueOf(200000.0))
                .build();

        Page<Coupon> couponPage = new PageImpl<>(List.of(coupon), pageRequest, 1);
        when(couponRepository.findAll(pageRequest)).
                thenReturn(couponPage);
        Page<CouponResponse> result = couponAdminService.getAllCoupons(pageRequest, null);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(couponRepository, times(1)).findAll(pageRequest);
        verify(couponRepository, never()).findByCodeContainingIgnoreCase(anyString(), any());
    }

    // Case 2: Keyword là rỗng
    @Test
    void getAllCoupons_ShouldCallFindAll_WhenKeywordIsEmpty() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .type("fixed")
                .code("MX300")
                .minOrderValue(BigDecimal.valueOf(100.0))
                .isPublic(true)
                .isSent(false)
                .isActive(true)
                .expiryDate(LocalDate.parse("2025-09-08"))
                .quantity(1)
                .value(BigDecimal.valueOf(200000.0))
                .build();

        Page<Coupon> couponPage = new PageImpl<>(List.of(coupon), pageRequest, 1);
        when(couponRepository.findAll(pageRequest)).
                thenReturn(couponPage);
        Page<CouponResponse> result = couponAdminService.getAllCoupons(pageRequest, "");
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(couponRepository, times(1)).findAll(pageRequest);
        verify(couponRepository, never()).findByCodeContainingIgnoreCase(anyString(), any());
    }

    // Case 3: Keyword không rỗng | null
    @Test
    void getAllCoupons_ShouldCallSearch_WhenKeywordIsNotEmpty() {
        Coupon coupon1 = Coupon.builder()
                .id(1L)
                .type("fixed")
                .code("MX300")
                .minOrderValue(BigDecimal.valueOf(100.0))
                .isPublic(true)
                .isSent(false)
                .isActive(true)
                .expiryDate(LocalDate.parse("2025-09-08"))
                .quantity(1)
                .value(BigDecimal.valueOf(200000.0))
                .build();
        Coupon coupon2 = Coupon.builder()
                .id(2L)
                .type("fixed")
                .code("MX500")
                .minOrderValue(BigDecimal.valueOf(100.0))
                .isPublic(true)
                .isSent(false)
                .isActive(true)
                .expiryDate(LocalDate.parse("2025-09-08"))
                .quantity(1)
                .value(BigDecimal.valueOf(200000.0))
                .build();
        Page<Coupon> couponPage = new PageImpl<>(List.of(coupon1, coupon2), pageRequest, 2);
        when(couponRepository.findByCodeContainingIgnoreCase("MX", pageRequest)).
                thenReturn(couponPage);
        Page<CouponResponse> result = couponAdminService.getAllCoupons(pageRequest, "MX");
        assertEquals(2, result.getTotalElements());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("MX300", result.getContent().get(0).getCode());
        verify(couponRepository, times(1)).findByCodeContainingIgnoreCase("MX", pageRequest);
        verify(couponRepository, never()).findAll(pageRequest);
    }

    //Case 4: Coupon hết hạn
    @Test
    void getAllCoupons_ShouldDeactivateExpiredCoupons() {
        Coupon expired = Coupon.builder()
                .id(1L)
                .code("OLD")
                .expiryDate(LocalDate.now().minusDays(1)) // hết hạn
                .isActive(true)
                .build();

        Page<Coupon> couponPage = new PageImpl<>(List.of(expired), pageRequest, 1);
        when(couponRepository.findAll(pageRequest)).thenReturn(couponPage);
        Page<CouponResponse> result = couponAdminService.getAllCoupons(pageRequest, null);

        assertFalse(result.getContent().get(0).isActive());
        verify(couponRepository).save(expired);
    }

    // Test check mã giảm giá
    //Case 1: Không tìm thấy user
    @Test
    void checkCoupon_ShouldThrowDataNotFoundException_WhenUserDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .fullname("Thuan")
                .address("Quang Binh")
                .build();
        when(userRepository.existsById(user.getId())).thenReturn(false);
        when(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)).
                thenReturn("Không tìm thấy người dùng");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.checkCoupon(user.getId(), anyString()));

        assertEquals("Không tìm thấy người dùng", exception.getMessage());
        verify(couponRepository, never()).save(any());
    }

    //Case 2: Không tìm thấy mã coupon
    @Test
    void checkCoupon_ShouldThrowDataNotFoundException_WhenCouponDoesNotExist() {
        Long userId = 1L;
        String code = "OLD";
        when(userRepository.existsById(userId)).thenReturn(true);
        // Giả lập coupon không tồn tại
        when(couponRepository.findByCode(eq(code))).thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND))
                .thenReturn("Mã giảm giá không hợp lệ");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.checkCoupon(userId, code));

        assertEquals("Mã giảm giá không hợp lệ", exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
        verify(couponRepository, never()).save(any());
    }

    //Case 3: Mã giảm giá hết hạn
    @Test
    void checkCoupon_ShouldThrowException_WhenCouponExpired() {
        // given
        Long userId = 1L;
        String code = "MX999";
        Coupon expiredCoupon = Coupon.builder()
                .id(1L)
                .code("MX999")
                .expiryDate(LocalDate.now().minusDays(1)) // hết hạn hôm qua
                .isActive(true)
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(couponRepository.findByCode(eq(code))).thenReturn(Optional.of(expiredCoupon));
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_EXPIRED))
                .thenReturn("Mã giảm giá đã hết hạn");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.checkCoupon(userId, code));

        assertEquals("Mã giảm giá đã hết hạn", exception.getMessage());
        assertFalse(expiredCoupon.isActive());
        verify(userRepository, times(1)).existsById(userId);
        verify(couponRepository, times(1)).save(expiredCoupon);
    }

    // User không có quyền sử dụng mã giảm này
    @Test
    void checkCoupon_ShouldThrowException_WhenPrivateCouponNotAssignedToUser() {
        Long userId = 1L;
        String code = "PRIVATE123";
        Coupon privateCoupon = Coupon.builder()
                .id(10L)
                .code(code)
                .expiryDate(LocalDate.now().plusDays(5)) // còn hạn
                .isActive(true)
                .isPublic(false) // mã riêng
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);

        when(couponRepository.findByCode(eq(code))).thenReturn(Optional.of(privateCoupon));

        when(userCouponRepository.findByUserIdAndCouponId(userId, privateCoupon.getId()))
                .thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_ACCESS_DENIED))
                .thenReturn("Bạn không có quyền sử dụng mã này");
        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.checkCoupon(userId, code));

        assertEquals("Bạn không có quyền sử dụng mã này", exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
        verify(couponRepository, times(1)).findByCode(code);
        verify(userCouponRepository, times(1)).findByUserIdAndCouponId(userId, privateCoupon.getId());
        verify(couponRepository, never()).save(any());
    }

    //Case 5: Mã giảm giá đã được sử dụng
    @Test
    void checkCoupon_ShouldThrowException_WhenCouponAlreadyUsed() {
        Long userId = 1L;
        String code = "PRIVATE123";
        Coupon privateCoupon = Coupon.builder()
                .id(10L)
                .code(code)
                .expiryDate(LocalDate.now().plusDays(5))
                .isActive(true)
                .isPublic(true)
                .build();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(couponRepository.findByCode(eq(code))).thenReturn(Optional.of(privateCoupon));
        when(userCouponRepository.existsByUserIdAndCouponIdAndStatus(userId, privateCoupon.getId(), CouponStatus.USED))
                .thenReturn(true);
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_ALREADY_USED))
                .thenReturn("Mã giảm giá này đã được sử dụng");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.checkCoupon(userId, code));
        assertEquals("Mã giảm giá này đã được sử dụng", exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
        verify(couponRepository, times(1)).findByCode(code);
        verify(userCouponRepository, times(1)).existsByUserIdAndCouponIdAndStatus(
                userId, privateCoupon.getId(), CouponStatus.USED);
        verify(couponRepository, never()).save(privateCoupon);
    }

    //Case 6: Mã giảm giá hợp lệ (public, chưa hết hạn, chưa dùng)
    @Test
    void checkCoupon_ShouldReturnCouponResponse_WhenCouponIsValid() throws Exception {
        Long userId = 1L;
        String code = "VALID123";
        Coupon coupon = Coupon.builder()
                .id(30L)
                .code(code)
                .expiryDate(LocalDate.now().plusDays(5))
                .isActive(true)
                .isPublic(true) // public, không check UserCoupon
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(couponRepository.findByCode(eq(code))).thenReturn(Optional.of(coupon));
        when(userCouponRepository.existsByUserIdAndCouponIdAndStatus(userId, coupon.getId(), CouponStatus.USED))
                .thenReturn(false);

        CouponResponse response = couponAdminService.checkCoupon(userId, code);

        assertNotNull(response);
        assertEquals(code, response.getCode());

        verify(userRepository, times(1)).existsById(userId);
        verify(couponRepository, times(1)).findByCode(code);
        verify(userCouponRepository, times(1)).existsByUserIdAndCouponIdAndStatus(
                userId, coupon.getId(), CouponStatus.USED);
    }

    //Test Đảo ngược giá trị Active
    //Case 1: Không tìm thấy mã giảm giá
    @Test
    void toggleCouponStatus_ShouldThrowException_WhenCouponNotFound() {
        Long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND))
                .thenReturn("Không tìm thấy mã giảm giá");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.toggleCouponStatus(couponId));

        assertEquals("Không tìm thấy mã giảm giá", exception.getMessage());
        verify(couponRepository, times(1)).findById(couponId);
        verify(couponRepository, never()).save(any());
    }

    //Case 2 : Đảo ngược trạng thai từ active -> inactive
    @Test
    void toggleCouponStatus_ShouldToggleFromTrueToFalse_WhenCouponExistsAndActive() throws Exception {
        Long couponId = 30L;
        Coupon coupon = Coupon.builder()
                .id(30L)
                .expiryDate(LocalDate.now().plusDays(5))
                .isActive(true)
                .isPublic(true) // public, không check UserCoupon
                .build();
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        CouponResponse couponResponse = couponAdminService.toggleCouponStatus(couponId);

        assertNotNull(couponResponse);
        assertEquals(couponId, couponResponse.getId());
        assertFalse(couponResponse.isActive());
        verify(couponRepository, times(1)).save(coupon);

    }

    //Case 3 : Đảo ngược trạng thái từ inactive -> active
    @Test
    void toggleCouponStatus_ShouldToggleFromFalseToTrueWhenCouponExistsAndInActive() throws Exception {
        Long couponId = 30L;
        Coupon coupon = Coupon.builder()
                .id(30L)
                .expiryDate(LocalDate.now().plusDays(5))
                .isActive(false)
                .isPublic(true)
                .build();
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        CouponResponse couponResponse = couponAdminService.toggleCouponStatus(couponId);

        assertNotNull(couponResponse);
        assertEquals(couponId, couponResponse.getId());
        assertTrue(couponResponse.isActive());
        verify(couponRepository, times(1)).save(coupon);
    }

    //Test gửi mã cho tất cả người dùng
    //Case 1 : Mã giảm giá không tồn tại
    @Test
    void sendCouponToAllUsers_ShouldThrowException_WhenCouponNotFound() {
        String couponCode = "MX33333";
        when(couponRepository.findByCode(couponCode)).
                thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND))
                .thenReturn("Mã giảm giá không tồn tại");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.sendCouponToAllUsers(couponCode));

        assertEquals("Mã giảm giá không tồn tại", exception.getMessage());
        verify(couponRepository, times(1)).findByCode(couponCode);
        verify(couponRepository, never()).save(any());
    }

    //Case 2: Gửi mã giảm giá thành công cho người dùng
    @Test
    void sendCouponToAllUsers_ShouldSendNotifications_WhenCouponExists() throws Exception {
        String couponCode = "SALE123";
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code(couponCode)
                .expiryDate(LocalDate.now().plusDays(3))
                .isActive(true)
                .isPublic(false)
                .isSent(false)
                .build();

        User user1 = new User();
        user1.setId(10L);
        User user2 = new User();
        user2.setId(20L);

        when(couponRepository.findByCode(couponCode)).thenReturn(Optional.of(coupon));
        when(userRepository.findAllExceptAdmin()).thenReturn(List.of(user1,user2));

        couponAdminService.sendCouponToAllUsers(couponCode);

        assertTrue(coupon.isPublic());
        assertTrue(coupon.isSent());
        verify(couponRepository,times(1)).findByCode(couponCode);
        verify(couponRepository, times(1)).save(coupon);
        verify(userRepository,times(1)).findAllExceptAdmin();
        verify(notificationRepository,times(2)).save(any());
        ArgumentCaptor<NotificationResponse> captor = ArgumentCaptor.forClass(NotificationResponse.class);
        verify(rabbitTemplate, times(2))
                .convertAndSend(eq("coupon.exchange"), eq("coupon.notify"), captor.capture());

        List<NotificationResponse> responses = captor.getAllValues();
        assertEquals("SALE123", responses.get(0).getContent().contains("SALE123") ? "SALE123" : "");
    }

    //Test gửi mã cho người dùng được chọn
    //Case 1 : Mã giảm giá không tồn tại
    @Test
    void sendCouponToUsers_ShouldThrowException_WhenCouponNotFound() {
        String couponCode = "MX33333";
        when(couponRepository.findByCode(couponCode)).
                thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.COUPON_NOT_FOUND))
                .thenReturn("Mã giảm giá không tồn tại");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> couponAdminService.sendCouponToAllUsers(couponCode));

        assertEquals("Mã giảm giá không tồn tại", exception.getMessage());
        verify(couponRepository, times(1)).findByCode(couponCode);
        verify(couponRepository, never()).save(any());
    }
    //Case 2 : Gửi mã giảm giá thành công cho người dùng
    @Test
    void sendCouponToUsers_ShouldSendNotifications_WhenCouponExists() throws Exception {
        SendCouponDto couponDto = new SendCouponDto();
        couponDto.setCouponCode("MX300");
        couponDto.setUserIds(List.of(1L, 2L));

        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("MX300")
                .expiryDate(LocalDate.now().plusDays(3))
                .isActive(true)
                .isPublic(false)
                .isSent(false)
                .build();

        User user1 = new User();
        user1.setId(10L);
        User user2 = new User();
        user2.setId(20L);

        when(couponRepository.findByCode(couponDto.getCouponCode())).thenReturn(Optional.of(coupon));
        when(userRepository.findAllById(couponDto.getUserIds()))
                .thenReturn(List.of(user1, user2));

        couponAdminService.sendCouponToUsers(couponDto);
        assertTrue(coupon.isSent());
        verify(couponRepository,times(1)).findByCode(couponDto.getCouponCode());
        verify(couponRepository,times(1)).save(coupon);
        verify(userRepository,times(1)).findAllById(couponDto.getUserIds());
        verify(userCouponRepository,times(2)).save(any());
        verify(notificationRepository,times(2)).save(any());
        ArgumentCaptor<NotificationResponse> captor = ArgumentCaptor.forClass(NotificationResponse.class);
        verify(rabbitTemplate, times(2))
                .convertAndSend(eq("coupon.exchange"), eq("coupon.notify"), captor.capture());

        List<NotificationResponse> responses = captor.getAllValues();
        assertEquals("MX300", responses.get(0).getContent().contains("MX300") ? "MX300" : "");
    }
}
