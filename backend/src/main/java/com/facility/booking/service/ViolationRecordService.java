package com.facility.booking.service;

import com.facility.booking.entity.Reservation;
import com.facility.booking.entity.ViolationRecord;
import com.facility.booking.repository.ViolationRecordRepository;
import com.facility.booking.repository.UserRepository;
import com.facility.booking.repository.ReservationRepository;
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


    /**
     * ????????????¦´????
     */
    @PostConstruct
    public void onStartup() {
        System.out.println("?????????????¦´????...");
        syncAllUserViolationStats();
        autoDetectViolations();
        System.out.println("?????¦´???????");
    }

    /**
     * ?????????????¦´????
     * ?5?????????¦²?????????¦´??
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional
    public void autoDetectViolations() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("?????????¦´??????????" + now);

        int overdueCount = 0;
        int noShowCount = 0;

        try {
            // ???????¦´??
            overdueCount = detectOverdueViolations(now);
            // ?????¦´??
            noShowCount = detectNoShowViolations(now);

            int totalCount = overdueCount + noShowCount;
            if (totalCount > 0) {
                System.out.println("???¦´??????????? " + totalCount + " ??¦´?—¤???????" + overdueCount + "??????" + noShowCount + ")");
            } else {
                System.out.println("???¦´????????¦Ä?????¦Ì?¦´????");
            }

        } catch (Exception e) {
            System.err.println("???¦´????????" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ???????¦´??
     */
    private int detectOverdueViolations(LocalDateTime now) {
        int count = 0;

        // ?????????????????????30??????¦Ä??????
        List<Reservation> overdueReservations = reservationRepository
                .findByCheckinStatusAndEndTimeBefore("CHECKED_IN", now.minusMinutes(30))
                .stream()
                .filter(reservation -> reservation.getCheckoutTime() == null) // ¦Ä???
                .collect(Collectors.toList());

        for (Reservation reservation : overdueReservations) {
            try {
                // ????????????????????????¦´????
                boolean exists = violationRecordRepository
                        .existsByReservationIdAndViolationType(reservation.getId(), "OVERDUE");

                if (!exists) {
                    // ??????????¦´????
                    ViolationRecord violationRecord = new ViolationRecord();
                    violationRecord.setUserId(reservation.getUserId());
                    violationRecord.setReservationId(reservation.getId());
                    violationRecord.setViolationType("OVERDUE");
                    violationRecord.setDescription("???????????????????30??????????¦Ä?????????" +
                            reservation.getStartTime() + " ?? " + reservation.getEndTime());
                    violationRecord.setPenaltyPoints(3);
                    violationRecord.setReportedBy(1L); // ????????
                    violationRecord.setReportedTime(now);
                    violationRecord.setStatus("PENDING");

                    violationRecordRepository.save(violationRecord);
                    count++;

                    // ????????? COMPLETED
                    reservation.setCheckinStatus("COMPLETED");
                    reservationRepository.save(reservation);

                    System.out.println("?????????¦´?¹×???ID=" + reservation.getUserId() +
                            ", ??ID=" + reservation.getId());
                }
            } catch (Exception e) {
                System.err.println("??????????¦´????????" + e.getMessage());
            }
        }

        return count;
    }

    /**
     * ?????¦´?—¤NO_SHOW??
     * ???????????15??????????¦Ä???
     */
    private int detectNoShowViolations(LocalDateTime now) {
        int count = 0;

        // ?????????????????????15??????¦Ä???????
        List<Reservation> noShowReservations = reservationRepository
                .findByStatusAndCheckinStatus("APPROVED", "NOT_CHECKED")
                .stream()
                .filter(reservation -> reservation.getStartTime() != null
                        && now.isAfter(reservation.getStartTime().plusMinutes(15)))
                .collect(Collectors.toList());

        for (Reservation reservation : noShowReservations) {
            try {
                // ????????????????????????¦´????
                boolean exists = violationRecordRepository
                        .existsByReservationIdAndViolationType(reservation.getId(), "NO_SHOW");

                if (!exists) {
                    // ??????¦´????
                    ViolationRecord violationRecord = new ViolationRecord();
                    violationRecord.setUserId(reservation.getUserId());
                    violationRecord.setReservationId(reservation.getId());
                    violationRecord.setViolationType("NO_SHOW");
                    violationRecord.setDescription("???????????????15??????????¦Ä??????????" +
                            reservation.getStartTime() + " ?? " + reservation.getEndTime());
                    violationRecord.setPenaltyPoints(5);
                    violationRecord.setReportedBy(1L); // ????????
                    violationRecord.setReportedTime(now);
                    violationRecord.setStatus("PENDING");

                    violationRecordRepository.save(violationRecord);
                    count++;

                    // ????????? MISSED
                    reservation.setCheckinStatus("MISSED");
                    reservationRepository.save(reservation);

                    System.out.println("?????¦´?¹×???ID=" + reservation.getUserId() +
                            ", ??ID=" + reservation.getId());
                }
            } catch (Exception e) {
                System.err.println("??????¦´????????" + e.getMessage());
            }
        }

        return count;
    }

    /**
     * ???¦´??
     * ?????? PENDING???????????????????????
     */
    @Transactional
    public ViolationRecord recordViolation(ViolationRecord violationRecord) {
        // ???????
        if (violationRecord == null) {
            throw new IllegalArgumentException("¦´???????????");
        }
        if (violationRecord.getUserId() == null) {
            throw new IllegalArgumentException("???ID???????");
        }

        // ?????????? PENDING????????
        violationRecord.setStatus("PENDING");

        // ???¦´????????????????¦¶??
        String[] allowedTypes = {"NO_SHOW", "OVERDUE", "CANCEL_FREQ", "DAMAGE", "OTHER"};
        if (violationRecord.getViolationType() != null && !java.util.Arrays.asList(allowedTypes).contains(violationRecord.getViolationType())) {
            violationRecord.setViolationType("OTHER");
        }

        // ????????????
        if (violationRecord.getPenaltyPoints() == null) {
            violationRecord.setPenaltyPoints(0);
        }

        // ????¦´?????????PENDING???????????????????
        ViolationRecord savedViolation = violationRecordRepository.save(violationRecord);

        return savedViolation;
    }


    /**
     * ?????????????¡Â?
     * ???¡Â? = 100 - ????????§¹??PROCESSED????¦´?????????????
     * ???????credit_score??????100??????????????¨®???????¡Â?
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
     * ????????????§¹??PROCESSED????????
     */
    public Integer getProcessedPenaltyPoints(Long userId) {
        Integer penalty = violationRecordRepository.sumProcessedPenaltyPointsByUserId(userId);
        return penalty != null ? penalty : 0;
    }

    /**
     * ????????¦´????
     */
    public Page<ViolationRecord> getUserViolations(Long userId, Pageable pageable) {
        Page<ViolationRecord> violations = violationRecordRepository.findByUserIdOrderByReportedTimeDesc(userId, pageable);
        // ?????????????????
        violations.forEach(violation -> {
            userRepository.findById(violation.getUserId()).ifPresent(user ->
                    violation.setUserName(user.getName()));
            if (violation.getReportedBy() != null) {
                userRepository.findById(violation.getReportedBy()).ifPresent(user ->
                        violation.setReporterName(user.getName()));
            }
            // ???????????????????ID??
            if (violation.getReservationId() != null) {
                // ?????????????ID????????????????????????????
            }
        });
        return violations;
    }

    /**
     * ??????????¦´????????
     */
    public Long getActiveViolationCount(Long userId) {
        Integer count = violationRecordRepository.countProcessedViolationsByUserId(userId);
        return count != null ? count.longValue() : 0L;
    }

    /**
     * ????????????????????????????????????
     */
    public Integer getTotalPenaltyPoints(Long userId) {
        Integer penalty = violationRecordRepository.sumPenaltyPointsByUserId(userId);
        return penalty != null ? penalty : 0;
    }

    /**
     * ????????¦´????
     * ??????????? PROCESSED?????????????????
     *
     * @param violationId ¦´????ID
     * @param adminId     ?????ID
     * @param remark      ?????
     * @return ???????
     */
    @Transactional
    public Map<String, Object> approveViolation(Long violationId, Long adminId, String remark) {
        Map<String, Object> result = new HashMap<>();

        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(violationId);
        if (!violationOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "¦´??????????");
            return result;
        }

        ViolationRecord violation = violationOpt.get();

        // ????????? PENDING
        if (!"PENDING".equals(violation.getStatus())) {
            result.put("success", false);
            result.put("message", "??¦´????????????????????");
            return result;
        }

        violation.setStatus("PROCESSED");
        violation.setRemark(remark);
        violation.setReportedBy(adminId);
        violationRecordRepository.save(violation);

        Long userId = violation.getUserId();
        recalculateUserCreditScoreAndViolationCount(userId);
        userRepository.findById(userId).ifPresent(user -> checkAndAddToBlacklist(user, adminId));


        result.put("success", true);
        result.put("message", "¦´???????????????????" + violation.getPenaltyPoints() + "??");
        result.put("violation", violation);

        return result;
    }

    /**
     * ????????¦´????
     * ???????????? REJECTED
     *
     * @param violationId ¦´????ID
     * @param adminId     ?????ID
     * @param remark      ?????????????
     * @return ???????
     */
    @Transactional
    public Map<String, Object> rejectViolation(Long violationId, Long adminId, String remark) {
        Map<String, Object> result = new HashMap<>();

        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(violationId);
        if (!violationOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "¦´??????????");
            return result;
        }

        ViolationRecord violation = violationOpt.get();

        // ????????? PENDING
        if (!"PENDING".equals(violation.getStatus())) {
            result.put("success", false);
            result.put("message", "??¦´?????????????????????");
            return result;
        }

        violation.setStatus("REJECTED");
        violation.setRemark(remark);
        violation.setReportedBy(adminId);
        violationRecordRepository.save(violation);
        recalculateUserCreditScoreAndViolationCount(violation.getUserId());


        result.put("success", true);
        result.put("message", "¦´??????");
        result.put("violation", violation);

        return result;
    }

    /**
     * ????????????§¹??¦´????
     *
     * @param violationId ¦´????ID
     * @param adminId     ?????ID
     * @param remark      ??????
     * @return ???????
     */
    @Transactional
    public Map<String, Object> revokeViolation(Long violationId, Long adminId, String remark) {
        Map<String, Object> result = new HashMap<>();

        Optional<ViolationRecord> violationOpt = violationRecordRepository.findById(violationId);
        if (!violationOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "¦´??????????");
            return result;
        }

        ViolationRecord violation = violationOpt.get();
        if (!"PROCESSED".equals(violation.getStatus())) {
            result.put("success", false);
            result.put("message", "??????????§¹??¦´????");
            return result;
        }

        violation.setStatus("REVOKED");
        violation.setRemark(remark);
        violation.setReportedBy(adminId);
        violationRecordRepository.save(violation);

        recalculateUserCreditScoreAndViolationCount(violation.getUserId());


        result.put("success", true);
        result.put("message", "¦´???????????§¹");
        result.put("violation", violation);

        return result;
    }

    /**
     * ?????????????????????
     * ???????????????????60???30????¦´??3??????
     */
    @Transactional
    public void checkAndAddToBlacklist(com.facility.booking.entity.User user, Long adminId) {
        if (user == null) return;

        recalculateUserCreditScoreAndViolationCount(user.getId());
        com.facility.booking.entity.User latestUser = userRepository.findById(user.getId()).orElse(user);
        Integer creditScore = latestUser.getCreditScore() != null ? latestUser.getCreditScore() : 100;

        // ?????????60??????????
        if (creditScore < 60) {
            addToBlacklist(latestUser.getId(), "?????????60??", 30, adminId);
            return;
        }

        // 30????¦´??3??????????????
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Integer recentViolations = violationRecordRepository.countRecentProcessedViolations(
                latestUser.getId(), "PROCESSED", thirtyDaysAgo);
        if (recentViolations != null && recentViolations >= 3) {
            addToBlacklist(latestUser.getId(), "30????¦´??" + recentViolations + "??", 7, adminId);
        }
    }

    /**
     * ??????????????
     */
    private void addToBlacklist(Long userId, String reason, int days, Long adminId) {
        try {
            blacklistService.addToBlacklist(userId, reason, days, adminId);
            System.out.println("???" + userId + "??" + reason + "??????????????");
        } catch (Exception e) {
            System.err.println("????????????????" + e.getMessage());
        }
    }

    /**
     * ????ID???¦´????????
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
     * ????¦´??????
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
     * ???????????????¦´????
     */
    public Page<ViolationRecord> getUserViolationsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        Page<ViolationRecord> violations = violationRecordRepository.findByUserIdAndTimeRange(userId, startTime, endTime, pageable);
        // ????????????????????
        violations.forEach(violation -> {
            userRepository.findById(violation.getUserId()).ifPresent(user ->
                    violation.setUserName(user.getName()));
            // ?????????????
            if (violation.getReportedBy() != null) {
                userRepository.findById(violation.getReportedBy()).ifPresent(user ->
                        violation.setReporterName(user.getName()));
            }
        });
        return violations;
    }

    /**
     * ???????¦´???????????????
     */
    public Page<ViolationRecord> getAllViolations(Pageable pageable, String userName, String violationType, String status) {
        try {
            // ????????????
            if (violationRecordRepository == null) {
                throw new RuntimeException("ViolationRecordRepository is null - database connection issue");
            }
            if (userRepository == null) {
                throw new RuntimeException("UserRepository is null - database connection issue");
            }

            System.out.println("Fetching violations with filters - userName: " + userName + ", violationType: " + violationType + ", status: " + status);

            // ????????????¨°???????????????????????????
            Page<ViolationRecord> violations;

            boolean hasUserName = userName != null && !userName.trim().isEmpty();
            boolean hasViolationType = violationType != null && !violationType.trim().isEmpty();
            boolean hasStatus = status != null && !status.trim().isEmpty();

            if (hasUserName && hasViolationType && hasStatus) {
                // ????????????
                violations = violationRecordRepository.findByFilters(userName, violationType, status, pageable);
            } else if (hasUserName && hasViolationType) {
                // ????? + ¦´??????
                violations = violationRecordRepository.findByUserNameAndViolationType(userName, violationType, pageable);
            } else if (hasUserName && hasStatus) {
                // ????? + ??
                violations = violationRecordRepository.findByUserNameAndStatus(userName, status, pageable);
            } else if (hasViolationType && hasStatus) {
                // ¦´?????? + ??
                violations = violationRecordRepository.findByViolationTypeAndStatus(violationType, status, pageable);
            } else if (hasUserName) {
                // ????????
                violations = violationRecordRepository.findByUserNameContainingIgnoreCase(userName, pageable);
            } else if (hasViolationType) {
                // ???¦´??????
                violations = violationRecordRepository.findByViolationType(violationType, pageable);
            } else if (hasStatus) {
                // ?????
                violations = violationRecordRepository.findByStatus(status, pageable);
            } else {
                // ?????????
                violations = violationRecordRepository.findAllByOrderByReportedTimeDesc(pageable);
            }

            // ??¦´???????
            violations.forEach(violation -> {
                try {
                    userRepository.findById(violation.getUserId()).ifPresent(user -> {
                        if (user.getName() != null) {
                            violation.setUserName(user.getName());
                        } else {
                            violation.setUserName("¦Ä????");
                        }
                    });
                    // ?????????????
                    if (violation.getReportedBy() != null) {
                        userRepository.findById(violation.getReportedBy()).ifPresent(user -> {
                            if (user.getName() != null) {
                                violation.setReporterName(user.getName());
                            } else {
                                violation.setReporterName("¦Ä??????");
                            }
                        });
                    }
                } catch (Exception e) {
                    // ???????????§Ø?????
                    System.err.println("Error enriching violation record " + violation.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });

            return violations;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("???????¦´???????: " + e.getMessage());
        }
    }

    /**
     * ?????????????¡Â?
     */
    public Integer getUserCurrentCreditScore(Long userId) {
        recalculateUserCreditScoreAndViolationCount(userId);
        return Math.max(0, 100 - getProcessedPenaltyPoints(userId));
    }

    /**
     * ????????¦´???????????????¦´??????????
     */
    public Integer getUserViolationCount(Long userId) {
        recalculateUserCreditScoreAndViolationCount(userId);
        Integer count = violationRecordRepository.countAllViolationsByUserId(userId);
        return count != null ? count : 0;
    }

    /**
     * ??????????§¹??¦´??????????????¦´??????????
     */
    public Integer getUserActiveViolationCount(Long userId) {
        Integer count = violationRecordRepository.countProcessedViolationsByUserId(userId);
        return count != null ? count : 0;
    }

    /**
     * ??????????????¦´????
     */
    public Page<ViolationRecord> getMaintainerViolations(Pageable pageable, Long maintainerId, String userName, String violationType, String status) {
        try {
            // ??????????????ID???????????????????????
            Page<ViolationRecord> violations;
            if (maintainerId != null) {
                violations = violationRecordRepository.findByReportedByOrderByReportedTimeDesc(maintainerId, pageable);
            } else {
                // ????????????????????????reportedBy??????????
                violations = violationRecordRepository.findByReportedByIsNotNullOrderByReportedTimeDesc(pageable);
            }

            // ??¦´???????
            violations.forEach(violation -> {
                try {
                    // ?????????
                    userRepository.findById(violation.getUserId()).ifPresent(user -> {
                        if (user.getName() != null) {
                            violation.setUserName(user.getName());
                        } else {
                            violation.setUserName("¦Ä????");
                        }
                    });

                    // ?????????????
                    if (violation.getReportedBy() != null) {
                        userRepository.findById(violation.getReportedBy()).ifPresent(user -> {
                            if (user.getName() != null) {
                                violation.setReporterName(user.getName());
                            } else {
                                violation.setReporterName("¦Ä??????");
                            }
                        });
                    }
                } catch (Exception e) {
                    // ???????????§Ø?????
                    System.err.println("Error enriching violation record " + violation.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // ????§Û???????????????§ß??§Û???
            if ((userName != null && !userName.trim().isEmpty()) ||
                    (violationType != null && !violationType.trim().isEmpty()) ||
                    (status != null && !status.trim().isEmpty())) {

                List<ViolationRecord> filteredList = violations.getContent().stream()
                        .filter(violation -> {
                            // ?????????
                            if (userName != null && !userName.trim().isEmpty()) {
                                if (violation.getUserName() == null ||
                                        !violation.getUserName().toLowerCase().contains(userName.toLowerCase())) {
                                    return false;
                                }
                            }

                            // ¦´?????????
                            if (violationType != null && !violationType.trim().isEmpty()) {
                                if (!violationType.equals(violation.getViolationType())) {
                                    return false;
                                }
                            }

                            // ??????
                            if (status != null && !status.trim().isEmpty()) {
                                if (!status.equals(violation.getStatus())) {
                                    return false;
                                }
                            }

                            return true;
                        })
                        .collect(Collectors.toList());

                // ?????¦Ì?Page????
                return new org.springframework.data.domain.PageImpl<>(
                        filteredList,
                        pageable,
                        filteredList.size()
                );
            }

            return violations;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("?????????¦´???????: " + e.getMessage());
        }
    }

    /**
     * ???¦´???????????
     * ???????????????????????????????????
     */
    public Map<String, Object> getViolationStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long totalViolations = violationRecordRepository.count();
            long pendingViolations = violationRecordRepository.countByStatusPending();
            int totalPenaltyPoints = violationRecordRepository.sumAllProcessedPenaltyPoints();

            stats.put("totalViolations", totalViolations);
            stats.put("pendingViolations", pendingViolations);
            stats.put("totalPenaltyPoints", totalPenaltyPoints);

            return stats;
        } catch (Exception e) {
            System.err.println("???¦´??????????????: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("???¦´??????????????: " + e.getMessage());
        }
    }
}
