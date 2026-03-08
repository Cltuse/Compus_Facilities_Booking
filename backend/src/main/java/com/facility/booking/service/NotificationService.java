package com.facility.booking.service;

import com.facility.booking.entity.Notification;
import com.facility.booking.repository.NotificationRepository;
import com.facility.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 发送通知
     */
    public Notification sendNotification(Notification notification) {
        notification.setStatus("UNREAD");
        notification.setReadTime(null);
        return notificationRepository.save(notification);
    }

    /**
     * 发送预约审核结果通知
     */
    public void sendReservationNotification(Long userId, String title, String content, Long reservationId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("RESERVATION");
        notification.setRelatedId(reservationId);
        notification.setRelatedType("RESERVATION");
        sendNotification(notification);
    }

    /**
     * 发送违规处罚通知
     */
    public void sendViolationNotification(Long userId, String title, String content, Long violationId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("VIOLATION");
        notification.setRelatedId(violationId);
        notification.setRelatedType("VIOLATION");
        sendNotification(notification);
    }

    /**
     * 发送反馈回复通知
     */
    public void sendFeedbackNotification(Long userId, String title, String content, Long feedbackId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("FEEDBACK");
        notification.setRelatedId(feedbackId);
        notification.setRelatedType("FEEDBACK");
        sendNotification(notification);
    }

    /**
     * 获取用户的通知列表
     */
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        // 设置用户名
        notifications.forEach(notification -> {
            userRepository.findById(notification.getUserId()).ifPresent(user -> 
                notification.setUserName(user.getName()));
        });
        return notifications;
    }

    /**
     * 获取用户的未读通知数量
     */
    public Long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }

    /**
     * 标记单个通知为已读
     */
    @Transactional
    public boolean markAsRead(Long id) {
        return notificationRepository.markAsRead(id, LocalDateTime.now()) > 0;
    }

    /**
     * 根据ID获取通知详情
     */
    public Optional<Notification> getNotificationById(Long id) {
        Optional<Notification> notificationOpt = notificationRepository.findById(id);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            userRepository.findById(notification.getUserId()).ifPresent(user -> 
                notification.setUserName(user.getName()));
        }
        return notificationOpt;
    }

    /**
     * 获取用户最近的通知
     */
    public List<Notification> getRecentNotifications(Long userId, LocalDateTime startTime) {
        return notificationRepository.findByUserIdAndTimeRange(userId, startTime);
    }
}