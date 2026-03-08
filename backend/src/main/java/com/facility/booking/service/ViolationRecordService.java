package com.facility.booking.service;

import com.facility.booking.entity.ViolationRecord;
import com.facility.booking.repository.ViolationRecordRepository;
import com.facility.booking.repository.UserRepository;
import com.facility.booking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ViolationRecordService {

    @Autowired
    private ViolationRecordRepository violationRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * 记录违规
     */
    public ViolationRecord recordViolation(ViolationRecord violationRecord) {
        violationRecord.setStatus("ACTIVE");
        return violationRecordRepository.save(violationRecord);
    }

    /**
     * 获取用户的违规记录
     */
    public Page<ViolationRecord> getUserViolations(Long userId, Pageable pageable) {
        Page<ViolationRecord> violations = violationRecordRepository.findByUserIdOrderByViolationTimeDesc(userId, pageable);
        // 设置用户名和操作员名
        violations.forEach(violation -> {
            userRepository.findById(violation.getUserId()).ifPresent(user -> 
                violation.setUserName(user.getName()));
            if (violation.getOperatorId() != null) {
                userRepository.findById(violation.getOperatorId()).ifPresent(user -> 
                    violation.setOperatorName(user.getName()));
            }
            // 设置设施名称（如果有预约ID）
            if (violation.getReservationId() != null) {
                // 这里需要根据预约ID获取设施名称，具体实现取决于数据结构
            }
        });
        return violations;
    }

    /**
     * 获取用户的活跃违规记录数量
     */
    public Long getActiveViolationCount(Long userId) {
        return violationRecordRepository.countActiveViolationsByUserId(userId);
    }

    /**
     * 获取用户的总扣分
     */
    public Integer getTotalCreditDeduction(Long userId) {
        Integer deduction = violationRecordRepository.sumActiveCreditDeductionByUserId(userId);
        return deduction != null ? deduction : 0;
    }

    /**
     * 根据ID获取违规记录详情
     */
    public Optional<ViolationRecord> getViolationById(Long id) {
        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(id);
        if (violationOpt.isPresent()) {
            ViolationRecord violation = violationOpt.get();
            userRepository.findById(violation.getUserId()).ifPresent(user -> 
                violation.setUserName(user.getName()));
            if (violation.getOperatorId() != null) {
                userRepository.findById(violation.getOperatorId()).ifPresent(user -> 
                    violation.setOperatorName(user.getName()));
            }
        }
        return violationOpt;
    }

    /**
     * 更新违规记录状态
     */
    @Transactional
    public boolean updateViolationStatus(Long id, String status, Long operatorId) {
        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(id);
        if (violationOpt.isPresent()) {
            ViolationRecord violation = violationOpt.get();
            violation.setStatus(status);
            violation.setOperatorId(operatorId);
            violationRecordRepository.save(violation);
            return true;
        }
        return false;
    }

    /**
     * 定时任务：检查并更新过期的违规记录
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天午夜执行
    @Transactional
    public void checkAndUpdateExpiredViolations() {
        List<ViolationRecord> expiredViolations = violationRecordRepository.findExpiredViolations(LocalDateTime.now());
        for (ViolationRecord violation : expiredViolations) {
            violation.setStatus("EXPIRED");
            violationRecordRepository.save(violation);
        }
    }

    /**
     * 获取用户某段时间内的违规记录
     */
    public Page<ViolationRecord> getUserViolationsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<ViolationRecord> violations = violationRecordRepository.findByUserIdAndTimeRange(userId, startTime, endTime, pageable);
        // 设置用户名
        violations.forEach(violation -> {
            userRepository.findById(violation.getUserId()).ifPresent(user -> 
                violation.setUserName(user.getName()));
            if (violation.getOperatorId() != null) {
                userRepository.findById(violation.getOperatorId()).ifPresent(user -> 
                    violation.setOperatorName(user.getName()));
            }
        });
        return violations;
    }
}