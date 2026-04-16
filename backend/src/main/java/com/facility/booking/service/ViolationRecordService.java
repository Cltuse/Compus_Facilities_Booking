package com.facility.booking.service;

import com.facility.booking.entity.Reservation;
import com.facility.booking.entity.ViolationRecord;
import com.facility.booking.entity.OperationLog;
import com.facility.booking.repository.ViolationRecordRepository;
import com.facility.booking.repository.UserRepository;
import com.facility.booking.repository.ReservationRepository;
import com.facility.booking.repository.OperationLogRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private OperationLogRepository operationLogRepository;

    /**
     * 系统启动时执行一次违规检测
     */
    @PostConstruct
    public void onStartup() {
        System.out.println("系统启动，开始执行违规检测...");
        syncAllUserViolationStats();
        autoDetectViolations();
        System.out.println("系统启动违规检测完成");
    }

    /**
     * 定时任务：自动检测违规记录
     * 每5分钟执行一次，只检测超时使用违规
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional
    public void autoDetectViolations() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("开始执行自动违规检测，当前时间：" + now);

        int overdueCount = 0;
        int noShowCount = 0;

        try {
            // 检测超时使用违规
            overdueCount = detectOverdueViolations(now);
            // 检测爽约违规
            noShowCount = detectNoShowViolations(now);

            int totalCount = overdueCount + noShowCount;
            if (totalCount > 0) {
                System.out.println("自动违规检测完成，检测到 " + totalCount + " 条违规（超时使用：" + overdueCount + "，爽约：" + noShowCount + ")");
            } else {
                System.out.println("自动违规检测完成，未发现新的违规记录");
            }

        } catch (Exception e) {
            System.err.println("自动违规检测失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检测超时使用违规
     */
    private int detectOverdueViolations(LocalDateTime now) {
        int count = 0;

        // 查找已签到但结束时间已过30分钟且未签退的预约
        List<Reservation> overdueReservations = reservationRepository
                .findByCheckinStatusAndEndTimeBefore("CHECKED_IN", now.minusMinutes(30))
                .stream()
                .filter(reservation -> reservation.getCheckoutTime() == null) // 未签退
                .collect(Collectors.toList());

        for (Reservation reservation : overdueReservations) {
            try {
                // 幂等性控制：检查是否已存在对应的违规记录
                boolean exists = violationRecordRepository
                        .existsByReservationIdAndViolationType(reservation.getId(), "OVERDUE");

                if (!exists) {
                    // 创建超时使用违规记录
                    ViolationRecord violationRecord = new ViolationRecord();
                    violationRecord.setUserId(reservation.getUserId());
                    violationRecord.setReservationId(reservation.getId());
                    violationRecord.setViolationType("OVERDUE");
                    violationRecord.setDescription("超时使用：预约结束时间已过30分钟，用户仍未签退。预约时间：" +
                            reservation.getStartTime() + " 至 " + reservation.getEndTime());
                    violationRecord.setPenaltyPoints(3);
                    violationRecord.setReportedBy(1L); // 系统自动上报
                    violationRecord.setReportedTime(now);
                    violationRecord.setStatus("PENDING");

                    violationRecordRepository.save(violationRecord);
                    count++;

                    // 更新预约状态为 COMPLETED
                    reservation.setCheckinStatus("COMPLETED");
                    reservationRepository.save(reservation);

                    System.out.println("检测到超时使用违规：用户ID=" + reservation.getUserId() +
                            ", 预约ID=" + reservation.getId());
                }
            } catch (Exception e) {
                System.err.println("创建超时使用违规记录失败：" + e.getMessage());
            }
        }

        return count;
    }

    /**
     * 检测爽约违规（NO_SHOW）
     * 预约开始时间已过15分钟，用户仍未签到
     */
    private int detectNoShowViolations(LocalDateTime now) {
        int count = 0;

        // 查找已批准但超过开始时间15分钟仍未签到的预约
        List<Reservation> noShowReservations = reservationRepository
                .findByStatusAndCheckinStatus("APPROVED", "NOT_CHECKED")
                .stream()
                .filter(reservation -> reservation.getStartTime() != null
                        && now.isAfter(reservation.getStartTime().plusMinutes(15)))
                .collect(Collectors.toList());

        for (Reservation reservation : noShowReservations) {
            try {
                // 幂等性控制：检查是否已存在对应的违规记录
                boolean exists = violationRecordRepository
                        .existsByReservationIdAndViolationType(reservation.getId(), "NO_SHOW");

                if (!exists) {
                    // 创建爽约违规记录
                    ViolationRecord violationRecord = new ViolationRecord();
                    violationRecord.setUserId(reservation.getUserId());
                    violationRecord.setReservationId(reservation.getId());
                    violationRecord.setViolationType("NO_SHOW");
                    violationRecord.setDescription("爽约：预约开始时间已过15分钟，用户仍未签到。预约时间：" +
                            reservation.getStartTime() + " 至 " + reservation.getEndTime());
                    violationRecord.setPenaltyPoints(5);
                    violationRecord.setReportedBy(1L); // 系统自动上报
                    violationRecord.setReportedTime(now);
                    violationRecord.setStatus("PENDING");

                    violationRecordRepository.save(violationRecord);
                    count++;

                    // 更新预约状态为 MISSED
                    reservation.setCheckinStatus("MISSED");
                    reservationRepository.save(reservation);

                    System.out.println("检测到爽约违规：用户ID=" + reservation.getUserId() +
                            ", 预约ID=" + reservation.getId());
                }
            } catch (Exception e) {
                System.err.println("创建爽约违规记录失败：" + e.getMessage());
            }
        }

        return count;
    }

    /**
     * 记录违规
     * 初始状态为 PENDING，不立即扣分，等待管理员审核
     */
    @Transactional
    public ViolationRecord recordViolation(ViolationRecord violationRecord) {
        // 参数验证
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }
        if (violationRecord.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 设置默认状态为 PENDING（待审核）
        violationRecord.setStatus("PENDING");

        // 验证违规类型是否在允许范围内
        String[] allowedTypes = {"NO_SHOW", "OVERDUE", "CANCEL_FREQ", "DAMAGE", "OTHER"};
        if (violationRecord.getViolationType() != null && !java.util.Arrays.asList(allowedTypes).contains(violationRecord.getViolationType())) {
            violationRecord.setViolationType("OTHER");
        }

        // 设置默认处罚分
        if (violationRecord.getPenaltyPoints() == null) {
            violationRecord.setPenaltyPoints(0);
        }

        // 保存违规记录（状态为PENDING，不立即计算信誉分）
        ViolationRecord savedViolation = violationRecordRepository.save(violationRecord);

        return savedViolation;
    }


    /**
     * 重新计算用户信用分
     * 信用分 = 100 - 所有已生效（PROCESSED状态）违规记录处罚分总和
     * 用户表中credit_score默认保持100不变，通过计算实时得出当前信用分
     */
    @Transactional
    public void recalculateUserCreditScoreAndViolationCount(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            Integer totalPenaltyPoints = getProcessedPenaltyPoints(userId);
            Integer totalViolationCount = violationRecordRepository.countAllViolationsByUserId(userId);
            user.setCreditScore(Math.max(0, 100 - totalPenaltyPoints));
            user.setViolationCount(totalViolationCount != null ? totalViolationCount : 0);
            userRepository.save(user);
        });
    }

    @Transactional
    public void syncAllUserViolationStats() {
        userRepository.findAll().forEach(user ->
                recalculateUserCreditScoreAndViolationCount(user.getId()));
    }

    /**
     * 获取用户的已生效（PROCESSED）处罚分
     */
    public Integer getProcessedPenaltyPoints(Long userId) {
        Integer penalty = violationRecordRepository.sumProcessedPenaltyPointsByUserId(userId);
        return penalty != null ? penalty : 0;
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
        Integer count = violationRecordRepository.countProcessedViolationsByUserId(userId);
        return count != null ? count.longValue() : 0L;
    }

    /**
     * 获取用户的总处罚分（包含所有状态，用于统计）
     */
    public Integer getTotalPenaltyPoints(Long userId) {
        Integer penalty = violationRecordRepository.sumPenaltyPointsByUserId(userId);
        return penalty != null ? penalty : 0;
    }

    /**
     * 管理员确认违规记录
     * 确认后将状态更新为 PROCESSED，并真正扣除信誉分
     *
     * @param violationId 违规记录ID
     * @param adminId     管理员ID
     * @param remark      审核备注
     * @return 操作结果
     */
    @Transactional
    public Map<String, Object> approveViolation(Long violationId, Long adminId, String remark) {
        Map<String, Object> result = new HashMap<>();

        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(violationId);
        if (!violationOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "违规记录不存在");
            return result;
        }

        ViolationRecord violation = violationOpt.get();

        // 检查状态是否为 PENDING
        if (!"PENDING".equals(violation.getStatus())) {
            result.put("success", false);
            result.put("message", "该违规记录已处理，不能重复确认");
            return result;
        }

        violation.setStatus("PROCESSED");
        violation.setRemark(remark);
        violation.setReportedBy(adminId);
        violationRecordRepository.save(violation);

        Long userId = violation.getUserId();
        recalculateUserCreditScoreAndViolationCount(userId);
        userRepository.findById(userId).ifPresent(user -> checkAndAddToBlacklist(user, adminId));

        // 记录操作日志
        logViolationOperation("APPROVE_VIOLATION", violationId, userId,
                "管理员确认违规，扣除信誉分" + violation.getPenaltyPoints() + "分", adminId);

        result.put("success", true);
        result.put("message", "违规确认成功，已扣除信誉分" + violation.getPenaltyPoints() + "分");
        result.put("violation", violation);

        return result;
    }

    /**
     * 管理员拒绝违规记录
     * 拒绝后将状态更新为 REJECTED
     *
     * @param violationId 违规记录ID
     * @param adminId     管理员ID
     * @param remark      审核备注（拒绝原因）
     * @return 操作结果
     */
    @Transactional
    public Map<String, Object> rejectViolation(Long violationId, Long adminId, String remark) {
        Map<String, Object> result = new HashMap<>();

        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(violationId);
        if (!violationOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "违规记录不存在");
            return result;
        }

        ViolationRecord violation = violationOpt.get();

        // 检查状态是否为 PENDING
        if (!"PENDING".equals(violation.getStatus())) {
            result.put("success", false);
            result.put("message", "该违规记录已处理，不能重复操作");
            return result;
        }

        violation.setStatus("REJECTED");
        violation.setRemark(remark);
        violation.setReportedBy(adminId);
        violationRecordRepository.save(violation);
        recalculateUserCreditScoreAndViolationCount(violation.getUserId());

        // 记录操作日志
        logViolationOperation("REJECT_VIOLATION", violationId, violation.getUserId(),
                "管理员拒绝违规，理由：" + remark, adminId);

        result.put("success", true);
        result.put("message", "违规已拒绝");
        result.put("violation", violation);

        return result;
    }

    /**
     * 管理员取消已生效的违规记录
     *
     * @param violationId 违规记录ID
     * @param adminId     管理员ID
     * @param remark      取消原因
     * @return 操作结果
     */
    @Transactional
    public Map<String, Object> revokeViolation(Long violationId, Long adminId, String remark) {
        Map<String, Object> result = new HashMap<>();

        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(violationId);
        if (!violationOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "违规记录不存在");
            return result;
        }

        ViolationRecord violation = violationOpt.get();
        if (!"PROCESSED".equals(violation.getStatus())) {
            result.put("success", false);
            result.put("message", "只能取消已生效的违规记录");
            return result;
        }

        violation.setStatus("REVOKED");
        violation.setRemark(remark);
        violation.setReportedBy(adminId);
        violationRecordRepository.save(violation);

        recalculateUserCreditScoreAndViolationCount(violation.getUserId());

        logViolationOperation("REVOKE_VIOLATION", violationId, violation.getUserId(),
                "管理员取消已生效违规，理由：" + (remark != null ? remark : "无"), adminId);

        result.put("success", true);
        result.put("message", "违规记录已取消生效");
        result.put("violation", violation);

        return result;
    }

    /**
     * 检查用户是否需要加入黑名单
     * 触发条件：信誉分低于60分或30天内违规3次以上
     */
    @Transactional
    public void checkAndAddToBlacklist(com.facility.booking.entity.User user, Long adminId) {
        if (user == null) return;

        recalculateUserCreditScoreAndViolationCount(user.getId());
        com.facility.booking.entity.User latestUser = userRepository.findById(user.getId()).orElse(user);
        Integer creditScore = latestUser.getCreditScore() != null ? latestUser.getCreditScore() : 100;

        // 信誉分低于60分，自动拉黑
        if (creditScore < 60) {
            addToBlacklist(latestUser.getId(), "信誉分低于60分", 30, adminId);
            return;
        }

        // 30天内违规3次以上，自动拉黑
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Integer recentViolations = violationRecordRepository.countRecentProcessedViolations(
                latestUser.getId(), "PROCESSED", thirtyDaysAgo);
        if (recentViolations != null && recentViolations >= 3) {
            addToBlacklist(latestUser.getId(), "30天内违规" + recentViolations + "次", 7, adminId);
        }
    }

    /**
     * 添加用户到黑名单
     */
    private void addToBlacklist(Long userId, String reason, int days, Long adminId) {
        try {
            blacklistService.addToBlacklist(userId, reason, days, adminId);
            System.out.println("用户" + userId + "因" + reason + "被自动加入黑名单");
        } catch (Exception e) {
            System.err.println("自动加入黑名单失败：" + e.getMessage());
        }
    }

    /**
     * 记录违规操作日志
     */
    private void logViolationOperation(String operationType, Long violationId, Long userId, String detail, Long operatorId) {
        try {
            OperationLog log = new OperationLog();
            log.setOperationType(operationType);
            log.setTargetId(violationId);
            log.setTargetType("VIOLATION");
            log.setDetail(detail);
            if (operatorId != null) {
                log.setOperatorId(operatorId);
            }
            operationLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("记录操作日志失败：" + e.getMessage());
        }
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
            String currentStatus = violation.getStatus();
            if ("PROCESSED".equals(status) && !"PROCESSED".equals(currentStatus)) {
                return Boolean.TRUE.equals(approveViolation(id, reportedBy, violation.getRemark()).get("success"));
            }
            if ("REJECTED".equals(status) && !"REJECTED".equals(currentStatus)) {
                return Boolean.TRUE.equals(rejectViolation(id, reportedBy, violation.getRemark()).get("success"));
            }
            if ("REVOKED".equals(status) && "PROCESSED".equals(currentStatus)) {
                return Boolean.TRUE.equals(revokeViolation(id, reportedBy, violation.getRemark()).get("success"));
            }
            violation.setStatus(status);
            violation.setReportedBy(reportedBy);
            violationRecordRepository.save(violation);
            recalculateUserCreditScoreAndViolationCount(violation.getUserId());
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

            // 根据筛选条件使用不同的查询方法，在数据库层面进行筛选
            Page<ViolationRecord> violations;

            boolean hasUserName = userName != null && !userName.trim().isEmpty();
            boolean hasViolationType = violationType != null && !violationType.trim().isEmpty();
            boolean hasStatus = status != null && !status.trim().isEmpty();

            if (hasUserName && hasViolationType && hasStatus) {
                // 三个条件都有
                violations = violationRecordRepository.findByFilters(userName, violationType, status, pageable);
            } else if (hasUserName && hasViolationType) {
                // 用户名 + 违规类型
                violations = violationRecordRepository.findByUserNameAndViolationType(userName, violationType, pageable);
            } else if (hasUserName && hasStatus) {
                // 用户名 + 状态
                violations = violationRecordRepository.findByUserNameAndStatus(userName, status, pageable);
            } else if (hasViolationType && hasStatus) {
                // 违规类型 + 状态
                violations = violationRecordRepository.findByViolationTypeAndStatus(violationType, status, pageable);
            } else if (hasUserName) {
                // 只有用户名
                violations = violationRecordRepository.findByUserNameContainingIgnoreCase(userName, pageable);
            } else if (hasViolationType) {
                // 只有违规类型
                violations = violationRecordRepository.findByViolationType(violationType, pageable);
            } else if (hasStatus) {
                // 只有状态
                violations = violationRecordRepository.findByStatus(status, pageable);
            } else {
                // 没有筛选条件
                violations = violationRecordRepository.findAllByOrderByReportedTimeDesc(pageable);
            }

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
        recalculateUserCreditScoreAndViolationCount(userId);
        return Math.max(0, 100 - getProcessedPenaltyPoints(userId));
    }

    /**
     * 获取用户的违规次数（所有状态的违规记录总数）
     */
    public Integer getUserViolationCount(Long userId) {
        recalculateUserCreditScoreAndViolationCount(userId);
        Integer count = violationRecordRepository.countAllViolationsByUserId(userId);
        return count != null ? count : 0;
    }

    /**
     * 获取用户的生效中违规数（所有状态的违规记录总数）
     */
    public Integer getUserActiveViolationCount(Long userId) {
        Integer count = violationRecordRepository.countProcessedViolationsByUserId(userId);
        return count != null ? count : 0;
    }

    /**
     * 获取维护人员上报的违规记录
     */
    public Page<ViolationRecord> getMaintainerViolations(Pageable pageable, Long maintainerId, String userName, String violationType, String status) {
        try {
            // 如果指定了维护人员ID，则只获取该维护人员上报的记录
            Page<ViolationRecord> violations;
            if (maintainerId != null) {
                violations = violationRecordRepository.findByReportedByOrderByReportedTimeDesc(maintainerId, pageable);
            } else {
                // 获取所有维护人员上报的记录（即reportedBy不为空的记录）
                violations = violationRecordRepository.findByReportedByIsNotNullOrderByReportedTimeDesc(pageable);
            }

            // 丰富违规记录信息
            violations.forEach(violation -> {
                try {
                    // 设置用户名
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
            throw new RuntimeException("获取维护人员违规记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取违规记录统计数据
     * 返回完整的统计数据，不受分页和筛选条件影响
     */
    public Map<String, Object> getViolationStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long totalViolations = violationRecordRepository.count();
            long pendingViolations = violationRecordRepository.countByStatusPending();
            int totalPenaltyPoints = violationRecordRepository.sumAllPenaltyPoints();

            stats.put("totalViolations", totalViolations);
            stats.put("pendingViolations", pendingViolations);
            stats.put("totalPenaltyPoints", totalPenaltyPoints);

            return stats;
        } catch (Exception e) {
            System.err.println("获取违规记录统计数据失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("获取违规记录统计数据失败: " + e.getMessage());
        }
    }
}
