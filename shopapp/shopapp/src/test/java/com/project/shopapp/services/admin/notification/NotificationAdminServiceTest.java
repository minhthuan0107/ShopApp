package com.project.shopapp.services.admin.notification;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Notification;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.NotificationRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.customer.notification.NotificationResponse;
import com.project.shopapp.ultis.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationAdminServiceTest {
    @Mock
    private LocalizationUtils localizationUtils;
    @InjectMocks
    private NotificationAdminService notificationAdminService;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test danh sách thông báo
    //Case 1: Không tìm thấy người dùng
    @Test
    void getNotificationByAdminId_ShouldThrowDataNotFoundException_WhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        when(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND))
                .thenReturn("Không tìm thấy người dùng");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> notificationAdminService.getNotificationByAdminId(userId));
        assertEquals("Không tìm thấy người dùng", exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
        verify(notificationRepository, never()).findByUserIdOrderByCreateAtDesc(userId);
    }

    //Case 2 : Trả về danh sách thông báo cho người dùng thành công
    @Test
    void getNotificationByAdminId_ShouldReturnNotificationList_WhenUserExists() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.existsById(userId)).thenReturn(true);

        Notification notification1 = Notification.builder()
                .id(1L)
                .title("Notification 1")
                .content("This is an announcement")
                .user(user)
                .build();

        Notification notification2 = Notification.builder()
                .id(2L)
                .title("Notification 1")
                .content("This is an announcement")
                .user(user)
                .build();
        when(notificationRepository.findByUserIdOrderByCreateAtDesc(userId))
                .thenReturn(List.of(notification1, notification2));

        List<NotificationResponse> notificationResponses = notificationAdminService.getNotificationByAdminId(userId);
        assertNotNull(notificationResponses);
        assertEquals(1L, notificationResponses.get(0).getId());
        assertEquals(2, notificationResponses.size());
        verify(userRepository, times(1)).existsById(userId);
        verify(notificationRepository, times(1)).findByUserIdOrderByCreateAtDesc(userId);
    }

    //Test đánh dấu thông báo đã đọc
    //Case 1: Không tìm thấy thông báo
    @Test
    void markAsReadNotification_ShouldThrowDataNotFoundException_WhenNotificationDoesNotExist() {
        Long userId = 2L;
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_NOT_FOUND, notificationId))
                .thenReturn("Không tìm thấy thông báo");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> notificationAdminService.markAsReadNotification(userId, notificationId));

        assertEquals("Không tìm thấy thông báo", exception.getMessage());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    //Test đánh dấu thông báo đã đọc
    //Case 2: Thông báo có tồn tại, nhưng không thuộc về userId truyền vào
    @Test
    void markAsReadNotification_ShouldThrowDataNotFoundException_WhenUserDoesNotOwnNotification() {
        Long userId = 2L;
        User user = new User();
        user.setId(1L);
        Long notificationId = 1L;
        Notification notification = Notification.builder()
                .id(1L)
                .title("Notification 1")
                .content("This is an announcement")
                .user(user)
                .build();
        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));
        when(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_ACCESS_DENIED))
                .thenReturn("Không có quyền truy cập thông báo này");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> notificationAdminService.markAsReadNotification(userId, notificationId));

        assertEquals("Không có quyền truy cập thông báo này", exception.getMessage());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    //Case 3:Thông báo tồn tại và thuộc về user
    @Test
    void markAsReadNotification_ShouldMarkNotificationAsRead_WhenNotificationExistsAndUserOwnsIt() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(1L);
        Long notificationId = 1L;
        Notification notification = Notification.builder()
                .id(1L)
                .title("Notification 1")
                .content("This is an announcement")
                .user(user)
                .build();
        when(notificationRepository.findById(notificationId)).thenReturn(
                Optional.of(notification));

        NotificationResponse result = notificationAdminService.markAsReadNotification(userId,notificationId);
        assertNotNull(result);
        assertTrue(result.getIsRead());
        assertEquals(1L, result.getId());
        verify(notificationRepository,times(1)).findById(notificationId);
        verify(notificationRepository,times(1)).save(notification);

    }
    //Case lấy thông báo chưa đọc theo adminId
    @Test
    void getUnreadNotificationsByAdminId_ShouldReturnUnreadNotifications_WhenUserIdIsValid(){
        Long userId = 1L;
        User user = new User();
        user.setId(1L);
        Notification notification1 = Notification.builder()
                .id(1L)
                .title("Notification 1")
                .content("This is an announcement")
                .user(user)
                .build();
        Notification notification2 = Notification.builder()
                .id(2L)
                .title("Notification 2")
                .content("This is an announcement")
                .user(user)
                .build();
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreateAtDesc(userId))
                .thenReturn(List.of(notification1,notification2));

        List<NotificationResponse> responses = notificationAdminService.getUnreadNotificationsByAdminId(userId);

        assertNotNull(responses);
        assertEquals(2,responses.size());
        assertEquals(1L,responses.get(0).getId());
        assertEquals("Notification 1",responses.get(0).getTitle());
        verify(notificationRepository,times(1)).findByUserIdAndIsReadFalseOrderByCreateAtDesc(userId);
    }
    //Test xóa thông báo theo userId
    //Case 1: Không tìm thấy thông báo theo notificaotionId
    @Test
    void deleteNoitificationById_ShouldThrowDataNotFoundException_WhenNotificationDoesNotExist() {
        Long userId = 1L;
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
        when(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_NOT_FOUND,notificationId))
                .thenReturn("Không tìm thấy thông báo");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                ()-> notificationAdminService.deleteNoitificationById(userId,notificationId));

        assertEquals("Không tìm thấy thông báo",exception.getMessage());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository,never()).delete(any());
    }
    //Case 2: User không có quyền xóa thông báo
    @Test
    void deleteNoitificationById_ShouldThrowDataNotFoundException_WhenUserDoesNotOwnNotification() {
        Long notificationId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(2L);
        Notification notification1 = Notification.builder()
                .id(1L)
                .title("Notification 1")
                .content("This is an announcement")
                .user(user)
                .build();
        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification1));
        when(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_DELETE_FORBIDDEN))
                .thenReturn("Bạn không có quyền xóa thông báo này");

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> notificationAdminService.deleteNoitificationById(userId, notificationId));

        assertEquals("Bạn không có quyền xóa thông báo này", exception.getMessage());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, never()).delete(notification1);
    }
        //Case 3: Xóa thông báo thành công
        @Test
        void DeleteNotificationById_ShouldDeleteNotification_WhenNotificationExistsAndUserOwnsIt() throws Exception{
            Long notificationId = 1L;
            Long userId = 2L;
            User user = new User();
            user.setId(2L);
            Notification notification1 = Notification.builder()
                    .id(1L)
                    .title("Notification 1")
                    .content("This is an announcement")
                    .user(user)
                    .build();
            when(notificationRepository.findById(notificationId))
                    .thenReturn(Optional.of(notification1));

            notificationAdminService.deleteNoitificationById(userId,notificationId);
            verify(notificationRepository,times(1)).findById(notificationId);
            verify(notificationRepository, times(1)).delete(notification1);

        }
    }


