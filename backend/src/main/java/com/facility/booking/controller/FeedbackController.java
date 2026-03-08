package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.entity.Feedback;
import com.facility.booking.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 提交反馈
     */
    @PostMapping("/submit")
    public Result submitFeedback(@RequestBody Feedback feedback) {
        try {
            Feedback savedFeedback = feedbackService.submitFeedback(feedback);
            return Result.success("反馈提交成功", savedFeedback);
        } catch (Exception e) {
            return Result.error("反馈提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的反馈列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserFeedbacks(@PathVariable Long userId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Feedback> feedbacks = feedbackService.getUserFeedbacks(userId, pageable);
            return Result.success("获取反馈列表成功", feedbacks);
        } catch (Exception e) {
            return Result.error("获取反馈列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取反馈详情
     */
    @GetMapping("/{id}")
    public Result getFeedbackDetail(@PathVariable Long id) {
        try {
            Optional<Feedback> feedback = feedbackService.getFeedbackById(id);
            if (feedback.isPresent()) {
                return Result.success("获取反馈详情成功", feedback.get());
            } else {
                return Result.error("反馈不存在");
            }
        } catch (Exception e) {
            return Result.error("获取反馈详情失败: " + e.getMessage());
        }
    }

    /**
     * 搜索用户的反馈
     */
    @GetMapping("/user/{userId}/search")
    public Result searchUserFeedbacks(@PathVariable Long userId,
                                      @RequestParam String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Feedback> feedbacks = feedbackService.searchUserFeedbacks(userId, keyword, pageable);
            return Result.success("搜索反馈成功", feedbacks);
        } catch (Exception e) {
            return Result.error("搜索反馈失败: " + e.getMessage());
        }
    }

    /**
     * 管理员回复反馈
     */
    @PostMapping("/{id}/reply")
    public Result replyFeedback(@PathVariable Long id,
                               @RequestParam String reply,
                               @RequestParam Long adminId) {
        try {
            boolean success = feedbackService.replyFeedback(id, reply, adminId);
            if (success) {
                return Result.success("回复反馈成功");
            } else {
                return Result.error("反馈不存在");
            }
        } catch (Exception e) {
            return Result.error("回复反馈失败: " + e.getMessage());
        }
    }

    /**
     * 获取待处理反馈数量
     */
    @GetMapping("/pending/count")
    public Result getPendingFeedbackCount() {
        try {
            Long count = feedbackService.getPendingFeedbackCount();
            return Result.success("获取待处理反馈数量成功", count);
        } catch (Exception e) {
            return Result.error("获取待处理反馈数量失败: " + e.getMessage());
        }
    }
}