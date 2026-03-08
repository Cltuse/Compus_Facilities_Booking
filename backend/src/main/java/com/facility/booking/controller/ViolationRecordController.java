package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.entity.ViolationRecord;
import com.facility.booking.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/violation")
public class ViolationRecordController {

    @Autowired
    private ViolationRecordService violationRecordService;

    /**
     * 记录违规
     */
    @PostMapping("/record")
    public Result recordViolation(@RequestBody ViolationRecord violationRecord) {
        try {
            ViolationRecord savedViolation = violationRecordService.recordViolation(violationRecord);
            return Result.success("违规记录成功", savedViolation);
        } catch (Exception e) {
            return Result.error("违规记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的违规记录列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserViolations(@PathVariable Long userId,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "violationTime"));
            Page<ViolationRecord> violations = violationRecordService.getUserViolations(userId, pageable);
            return Result.success("获取违规记录成功", violations);
        } catch (Exception e) {
            return Result.error("获取违规记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取违规记录详情
     */
    @GetMapping("/{id}")
    public Result getViolationDetail(@PathVariable Long id) {
        try {
            Optional<ViolationRecord> violation = violationRecordService.getViolationById(id);
            if (violation.isPresent()) {
                return Result.success("获取违规记录详情成功", violation.get());
            } else {
                return Result.error("违规记录不存在");
            }
        } catch (Exception e) {
            return Result.error("获取违规记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的活跃违规数量
     */
    @GetMapping("/user/{userId}/active/count")
    public Result getActiveViolationCount(@PathVariable Long userId) {
        try {
            Long count = violationRecordService.getActiveViolationCount(userId);
            return Result.success("获取活跃违规数量成功", count);
        } catch (Exception e) {
            return Result.error("获取活跃违规数量失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的总扣分
     */
    @GetMapping("/user/{userId}/total-deduction")
    public Result getTotalCreditDeduction(@PathVariable Long userId) {
        try {
            Integer deduction = violationRecordService.getTotalCreditDeduction(userId);
            return Result.success("获取总扣分成功", deduction);
        } catch (Exception e) {
            return Result.error("获取总扣分失败: " + e.getMessage());
        }
    }

    /**
     * 更新违规记录状态
     */
    @PutMapping("/{id}/status")
    public Result updateViolationStatus(@PathVariable Long id,
                                       @RequestParam String status,
                                       @RequestParam Long operatorId) {
        try {
            boolean success = violationRecordService.updateViolationStatus(id, status, operatorId);
            if (success) {
                return Result.success("更新违规记录状态成功");
            } else {
                return Result.error("违规记录不存在");
            }
        } catch (Exception e) {
            return Result.error("更新违规记录状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户某段时间内的违规记录
     */
    @GetMapping("/user/{userId}/time-range")
    public Result getUserViolationsByTimeRange(@PathVariable Long userId,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "violationTime"));
            Page<ViolationRecord> violations = violationRecordService.getUserViolationsByTimeRange(userId, startTime, endTime, pageable);
            return Result.success("获取时间段内违规记录成功", violations);
        } catch (Exception e) {
            return Result.error("获取时间段内违规记录失败: " + e.getMessage());
        }
    }
}