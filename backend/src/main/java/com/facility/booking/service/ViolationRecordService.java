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
import java.util.stream.Collectors;

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
    @Transactional
    public ViolationRecord recordViolation(ViolationRecord violationRecord) {
        violationRecord.setStatus("PENDING");
        
        // 保存违规记录
        ViolationRecord savedViolation = violationRecordRepository.save(violationRecord);
        
        // 更新用户信用分和违规次数
        userRepository.findById(violationRecord.getUserId()).ifPresent(user -> {
            // 减少信用分
            user.setCreditScore(user.getCreditScore() - (violationRecord.getPenaltyPoints() != null ? violationRecord.getPenaltyPoints() : 0));
            // 增加违规次数
            user.setViolationCount(user.getViolationCount() + 1);
            userRepository.save(user);
        });
        
        return savedViolation;
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
    public Page<ViolationRecord> getAllViolations(Pageable pageable, String userName, String violationType, String status) {
        try {
            // 验证数据库连接
            if (violationRecordRepository == null) {
                throw new RuntimeException("ViolationRecordRepository is null - database connection issue");
            }
            if (userRepository == null) {
                throw new RuntimeException("UserRepository is null - database connection issue");
            }
            
            System.out.println("Fetching violations with filters - userName: " + userName + ", violationType: " + violationType + ", status: " + status);
            
            // 先获取所有数据
            Page<ViolationRecord> violations = violationRecordRepository.findAllByOrderByReportedTimeDesc(pageable);
            
            // 丰富违规记录信息
            violations.forEach(violation -> {
                try {
                    userRepository.findById(violation.getUserId()).ifPresent(user -> {
                        if (user.getName() != null) {
                            violation.setUserName(user.getName());
                        } else {
                            violation.setUserName("未知用户");
                        }
                    });
                    // 设置上报人名称
                    if (violation.getReportedBy() != null) {
                        userRepository.findById(violation.getReportedBy()).ifPresent(user -> {
                            if (user.getName() != null) {
                                violation.setReporterName(user.getName());
                            } else {
                                violation.setReporterName("未知上报人");
                            }
                        });
                    }
                } catch (Exception e) {
                    // 记录日志但不中断流程
                    System.err.println("Error enriching violation record " + violation.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            // 如果有过滤条件，在内存中进行过滤
            if ((userName != null && !userName.trim().isEmpty()) || 
                (violationType != null && !violationType.trim().isEmpty()) || 
                (status != null && !status.trim().isEmpty())) {
                
                List<ViolationRecord> filteredList = violations.getContent().stream()
                    .filter(violation -> {
                        // 用户名过滤
                        if (userName != null && !userName.trim().isEmpty()) {
                            if (violation.getUserName() == null || 
                                !violation.getUserName().toLowerCase().contains(userName.toLowerCase())) {
                                return false;
                            }
                        }
                        
                        // 违规类型过滤
                        if (violationType != null && !violationType.trim().isEmpty()) {
                            if (!violationType.equals(violation.getViolationType())) {
                                return false;
                            }
                        }
                        
                        // 状态过滤
                        if (status != null && !status.trim().isEmpty()) {
                            if (!status.equals(violation.getStatus())) {
                                return false;
                            }
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
                
                // 创建新的Page对象
                return new org.springframework.data.domain.PageImpl<>(
                    filteredList, 
                    pageable, 
                    filteredList.size()
                );
            }
            
            return violations;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取所有违规记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的当前信用分
     */
    public Integer getUserCurrentCreditScore(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getCreditScore() != null ? user.getCreditScore() : 100)
                .orElse(100);
    }
    
    /**
     * 获取用户的违规次数
     */
    public Integer getUserViolationCount(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getViolationCount() != null ? user.getViolationCount() : 0)
                .orElse(0);
    }
}