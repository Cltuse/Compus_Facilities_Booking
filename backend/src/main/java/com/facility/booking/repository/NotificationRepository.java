package com.facility.booking.repository;

import com.facility.booking.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Page<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status, Pageable pageable);
    
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type, Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.status = 'UNREAD'")
    Long countUnreadByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readTime = :readTime WHERE n.userId = :userId AND n.status = 'UNREAD'")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);
    
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readTime = :readTime WHERE n.id = :id")
    int markAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime);
    
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt >= :startTime")
    List<Notification> findByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
}