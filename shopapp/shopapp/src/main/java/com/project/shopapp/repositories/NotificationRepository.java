package com.project.shopapp.repositories;

import com.project.shopapp.models.Favorite;
import com.project.shopapp.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUserIdOrderByCreateAtDesc(Long userId);

    @Query(value = "SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = false", nativeQuery = true)
    Long countNotificationItems (@Param("userId") Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreateAtDesc(Long userId);
}
