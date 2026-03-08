package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.entity.Notification;
import com.facility.booking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 发送通知
     */
    @PostMapping("/send")
    public Result sendNotification(@RequestBody Notification notification) {
        try {
            Notification savedNotification = notificationService.sendNotification(notification);
            return Result.success("发送通知成功", savedNotification);
        } catch (Exception e) {
            return Result.error("发送通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的通知列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserNotifications(@PathVariable Long userId,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
            return Result.success("获取通知列表成功", notifications);
        } catch (Exception e) {
            return Result.error("获取通知列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的未读通知数量
     */
    @GetMapping("/user/{userId}/unread/count")
    public Result getUnreadNotificationCount(@PathVariable Long userId) {
        try {
            Long count = notificationService.getUnreadNotificationCount(userId);
            return Result.success("获取未读通知数量成功", count);
        } catch (Exception e) {
            return Result.error("获取未读通知数量失败: " + e.getMessage());
        }
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/user/{userId}/mark-all-read")
    public Result markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return Result.success("标记所有通知为已读成功");
        } catch (Exception e) {
            return Result.error("标记所有通知为已读失败: " + e.getMessage());
        }
    }

    /**
     * 标记单个通知为已读
     */
    @PutMapping("/{id}/mark-read")
    public Result markAsRead(@PathVariable Long id) {
        try {
            boolean success = notificationService.markAsRead(id);
            if (success) {
                return Result.success("标记通知为已读成功");
            } else {
                return Result.error("通知不存在");
            }
        } catch (Exception e) {
            return Result.error("标记通知为已读失败: " + e.getMessage());
        }
    }

    /**
     * 获取通知详情
     */
    @GetMapping("/{id}")
    public Result getNotificationDetail(@PathVariable Long id) {
        try {
            Optional<Notification> notification = notificationService.getNotificationById(id);
            if (notification.isPresent()) {
                return Result.success("获取通知详情成功", notification.get());
            } else {
                return Result.error("通知不存在");
            }
        } catch (Exception e) {
            return Result.error("获取通知详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户最近的通知
     */
    @GetMapping("/user/{userId}/recent")
    public Result getRecentNotifications(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "7") int days) {
        try {
            LocalDateTime startTime = LocalDateTime.now().minusDays(days);
            List<Notification> notifications = notificationService.getRecentNotifications(userId, startTime);
            return Result.success("获取最近通知成功", notifications);
        } catch (Exception e) {
            return Result.error("获取最近通知失败: " + e.getMessage());
        }
    }
}