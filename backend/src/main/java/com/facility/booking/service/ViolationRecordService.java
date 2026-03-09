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
        violationRecord.setStatus("PENDING");
        return violationRecordRepository.save(violationRecord);
    }

    /**
     * 获取用户的违规记录
     */
    public Page<ViolationRecord> getUserViolations(Long userId, Pageable pageable) {
        Page<ViolationRecord> violations = violationRecordRepository.findByUserIdOrderByReportedTimeDesc(userId, pageable);
        // 设置用户名和操作员名
        violations.forEach(violation -> {
            userRepository.findById(violation.getUserId()).ifPresent(user -> 
                violation.setUserName(user.getName()));
            if (violation.getReportedBy() != null) {
                userRepository.findById(violation.getReportedBy()).ifPresent(user -> 
                    violation.setReporterName(user.getName()));
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
     * 获取用户的总处罚分
     */
    public Integer getTotalPenaltyPoints(Long userId) {
        Integer penalty = violationRecordRepository.sumPenaltyPointsByUserId(userId);
        return penalty != null ? penalty : 0;
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
            if (violation.getReportedBy() != null) {
                userRepository.findById(violation.getReportedBy()).ifPresent(user -> 
                    violation.setReporterName(user.getName()));
            }
        }
        return violationOpt;
    }

    /**
     * 更新违规记录状态
     */
    @Transactional
    public boolean updateViolationStatus(Long id, String status, Long reportedBy) {
        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(id);
        if (violationOpt.isPresent()) {
            ViolationRecord violation = violationOpt.get();
            violation.setStatus(status);
            violation.setReportedBy(reportedBy);
            violationRecordRepository.save(violation);
            return true;
        }
        return false;
    }



    /**
     * 获取用户某段时间内的违规记录
     */
    public Page<ViolationRecord> getUserViolationsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<ViolationRecord> violations = violationRecordRepository.findByUserIdAndTimeRange(userId, startTime, endTime, pageable);
        // 设置用户名和上报人名称
        violations.forEach(violation -> {
            userRepository.findById(violation.getUserId()).ifPresent(user -> 
                violation.setUserName(user.getName()));
            // 设置上报人名称
            if (violation.getReportedBy() != null) {
                userRepository.findById(violation.getReportedBy()).ifPresent(user -> 
                    violation.setReporterName(user.getName()));
            }
        });
        return violations;
    }

    /**
     * 获取所有违规记录（管理员使用）
     */
    public Page<ViolationRecord> getAllViolations(Pageable pageable) {
        try {
            Page<ViolationRecord> violations = violationRecordRepository.findAllByOrderByReportedTimeDesc(pageable);
            // 丰富违规记录信息
            violations.forEach(violation -> {
                userRepository.findById(violation.getUserId()).ifPresent(user -> 
                    violation.setUserName(user.getName()));
                // 设置上报人名称
                if (violation.getReportedBy() != null) {
                    userRepository.findById(violation.getReportedBy()).ifPresent(user -> 
                        violation.setReporterName(user.getName()));
                }
            });
            return violations;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取所有违规记录失败: " + e.getMessage());
        }
    }
}