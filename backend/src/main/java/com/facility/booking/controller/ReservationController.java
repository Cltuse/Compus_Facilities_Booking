package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.entity.Facility;
import com.facility.booking.entity.Reservation;
import com.facility.booking.entity.User;
import com.facility.booking.repository.FacilityRepository;
import com.facility.booking.repository.ReservationRepository;
import com.facility.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 设备预约控制器
 * 提供设备预约的增删改查、状态管理等功能
 */
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有预约记录
     * @return 预约记录列表
     */
    @GetMapping("/list")
    public Result<List<Reservation>> list() {
        List<Reservation> reservations = reservationRepository.findAll();
        enrichReservations(reservations);
        return Result.success(reservations);
    }

    /**
     * 根据用户ID获取预约记录
     * @param userId 用户ID
     * @return 该用户的预约记录列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Reservation>> getByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        enrichReservations(reservations);
        return Result.success(reservations);
    }

    /**
     * 获取待审核的预约记录
     * @return 待审核的预约记录列表
     */
    @GetMapping("/pending")
    public Result<List<Reservation>> getPending() {
        List<Reservation> reservations = reservationRepository.findByStatus("PENDING");
        enrichReservations(reservations);
        return Result.success(reservations);
    }

    /**
     * 根据ID获取预约记录详情
     * @param id 预约记录ID
     * @return 预约记录详情
     */
    @GetMapping("/{id}")
    public Result<Reservation> getById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            enrichReservation(res);
            return Result.success(res);
        }
        return Result.error("预约不存在");
    }

    /**
     * 创建预约记录
     * @param reservation 预约记录信息
     * @return 创建的预约记录信息
     */
    @PostMapping
    public Result<Reservation> create(@RequestBody Reservation reservation) {
        try {
            // 1. 基本数据验证
            if (reservation.getFacilityId() == null) {
                return Result.error("设施ID不能为空");
            }
            if (reservation.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            if (reservation.getStartTime() == null) {
                return Result.error("开始时间不能为空");
            }
            if (reservation.getEndTime() == null) {
                return Result.error("结束时间不能为空");
            }
            
            // 2. 检查设施是否存在
            Optional<Facility> facilityOpt = facilityRepository.findById(reservation.getFacilityId());
            if (!facilityOpt.isPresent()) {
                return Result.error("设施不存在");
            }
            
            // 3. 检查用户是否存在
            Optional<User> userOpt = userRepository.findById(reservation.getUserId());
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }
            
            // 4. 检查时间有效性
            if (reservation.getEndTime().isBefore(reservation.getStartTime())) {
                return Result.error("结束时间不能早于开始时间");
            }
            
            // 5. 检查时间冲突
            List<Reservation> existingReservations = reservationRepository.findByFacilityId(reservation.getFacilityId());
            for (Reservation existing : existingReservations) {
                if (isTimeConflict(reservation.getStartTime(), reservation.getEndTime(), 
                                 existing.getStartTime(), existing.getEndTime()) &&
                    !"REJECTED".equals(existing.getStatus()) && 
                    !"CANCELLED".equals(existing.getStatus())) {
                    return Result.error("该时间段已被预约，请选择其他时间");
                }
            }
            
            // 6. 设置默认状态并保存
            reservation.setStatus("PENDING");
            reservation.setCheckinStatus("NOT_CHECKED");
            
            Reservation savedReservation = reservationRepository.save(reservation);
            enrichReservation(savedReservation);
            return Result.success("预约成功", savedReservation);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("预约失败：" + e.getMessage());
        }
    }

    /**
     * 更新预约记录
     * @param id 预约记录ID
     * @param reservation 更新的预约记录信息
     * @return 更新后的预约记录信息
     */
    @PutMapping("/{id}")
    public Result<Reservation> update(@PathVariable Long id, @RequestBody Reservation reservation) {
        if (!reservationRepository.existsById(id)) {
            return Result.error("预约不存在");
        }
        reservation.setId(id);
        Reservation savedReservation = reservationRepository.save(reservation);
        enrichReservation(savedReservation);
        return Result.success("更新成功", savedReservation);
    }

    /**
     * 审核通过预约
     * @param id 预约记录ID
     * @param reservation 包含审核备注的预约记录信息
     * @return 审核通过的预约记录信息
     */
    @PutMapping("/{id}/approve")
    public Result<Reservation> approve(@PathVariable Long id, @RequestBody Reservation reservation) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();
        existingReservation.setStatus("APPROVED");
        existingReservation.setAdminRemark(reservation.getAdminRemark());

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("审核通过", savedReservation);
    }

    /**
     * 拒绝预约
     * @param id 预约记录ID
     * @param reservation 包含拒绝原因的预约记录信息
     * @return 被拒绝的预约记录信息
     */
    @PutMapping("/{id}/reject")
    public Result<Reservation> reject(@PathVariable Long id, @RequestBody Reservation reservation) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();
        existingReservation.setStatus("REJECTED");
        existingReservation.setAdminRemark(reservation.getAdminRemark());

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("已拒绝", savedReservation);
    }

    /**
     * 取消预约
     * @param id 预约记录ID
     * @return 取消后的预约记录信息
     */
    @PutMapping("/{id}/cancel")
    public Result<Reservation> cancel(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();
        existingReservation.setStatus("CANCELLED");

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("已取消", savedReservation);
    }

    /**
     * 搜索预约记录
     * @param keyword 搜索关键词
     * @return 符合条件的预约记录列表
     */
    @GetMapping("/search")
    public Result<List<Reservation>> search(@RequestParam(required = false) String keyword) {
        List<Reservation> reservations;

        if (keyword == null || keyword.trim().isEmpty()) {
            reservations = reservationRepository.findAll();
        } else {
            reservations = reservationRepository.findByKeyword(keyword.trim());
        }

        enrichReservations(reservations);
        return Result.success(reservations);
    }

    /**
     * 完成预约
     * @param id 预约记录ID
     * @return 完成后的预约记录信息
     */
    @PutMapping("/{id}/complete")
    public Result<Reservation> complete(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();

        // 只有已通过的预约才能完成
        if (!"APPROVED".equals(existingReservation.getStatus())) {
            return Result.error("只有已通过的预约才能完成");
        }

        // 更新预约状态为已完成
        existingReservation.setStatus("COMPLETED");

        // 更新设备状态为可用
        Optional<Facility> facilityOpt = facilityRepository.findById(existingReservation.getFacilityId());
        if (facilityOpt.isPresent()) {
            Facility facility = facilityOpt.get();
            facility.setStatus("AVAILABLE");
            facilityRepository.save(facility);
        }

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("预约已完成", savedReservation);
    }

    /**
     * 删除预约记录
     * @param id 预约记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!reservationRepository.existsById(id)) {
            return Result.error("预约不存在");
        }
        reservationRepository.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 用户签到
     * @param id 预约记录ID
     * @return 签到结果
     */
    @PutMapping("/{id}/checkin")
    public Result<Reservation> checkin(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation reservation = resOpt.get();
        
        // 检查预约状态
        if (!"APPROVED".equals(reservation.getStatus())) {
            return Result.error("只有已通过的预约才能签到");
        }
        
        // 检查签到状态
        if (!"NOT_CHECKED".equals(reservation.getCheckinStatus())) {
            return Result.error("该预约已签到或状态异常");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 检查是否在预约时间范围内（可提前15分钟签到）
        if (now.isBefore(reservation.getStartTime().minusMinutes(15))) {
            return Result.error("还未到签到时间，可提前15分钟签到");
        }
        
        if (now.isAfter(reservation.getEndTime())) {
            return Result.error("预约时间已结束，无法签到");
        }
        
        // 更新签到信息
        reservation.setCheckinStatus("CHECKED_IN");
        reservation.setCheckinTime(now);
        
        // 生成核销码
        String verificationCode = generateVerificationCode(id);
        reservation.setVerificationCode(verificationCode);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        enrichReservation(savedReservation);
        return Result.success("签到成功", savedReservation);
    }

    /**
     * 用户签退
     * @param id 预约记录ID
     * @return 签退结果
     */
    @PutMapping("/{id}/checkout")
    public Result<Reservation> checkout(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation reservation = resOpt.get();
        
        // 检查预约状态
        if (!"APPROVED".equals(reservation.getStatus())) {
            return Result.error("只有已通过的预约才能签退");
        }
        
        // 检查签到状态
        if (!"CHECKED_IN".equals(reservation.getCheckinStatus())) {
            return Result.error("请先签到后再签退");
        }
        
        // 更新签退信息
        reservation.setCheckinStatus("CHECKED_OUT");
        reservation.setCheckoutTime(LocalDateTime.now());
        
        Reservation savedReservation = reservationRepository.save(reservation);
        enrichReservation(savedReservation);
        return Result.success("签退成功", savedReservation);
    }

    /**
     * 管理员核销预约
     * @param id 预约记录ID
     * @param adminId 管理员ID
     * @param verificationCode 核销码
     * @return 核销结果
     */
    @PutMapping("/{id}/verify")
    public Result<Reservation> verify(@PathVariable Long id, 
                                     @RequestParam Long adminId,
                                     @RequestParam String verificationCode) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation reservation = resOpt.get();
        
        // 检查核销码
        if (!verificationCode.equals(reservation.getVerificationCode())) {
            return Result.error("核销码错误");
        }
        
        // 检查预约状态
        if (!"CHECKED_IN".equals(reservation.getCheckinStatus())) {
            return Result.error("该预约状态无法核销");
        }
        
        // 更新核销信息
        reservation.setVerifiedBy(adminId);
        reservation.setVerifiedTime(LocalDateTime.now());
        reservation.setCheckinStatus("CHECKED_OUT");
        reservation.setCheckoutTime(LocalDateTime.now());
        
        Reservation savedReservation = reservationRepository.save(reservation);
        enrichReservation(savedReservation);
        return Result.success("核销成功", savedReservation);
    }

    /**
     * 获取预约核销码
     * @param id 预约记录ID
     * @return 核销码
     */
    @GetMapping("/{id}/verification-code")
    public Result<Map<String, String>> getVerificationCode(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation reservation = resOpt.get();
        if (reservation.getVerificationCode() == null) {
            return Result.error("该预约暂无核销码");
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("verificationCode", reservation.getVerificationCode());
        return Result.success(result);
    }

    /**
     * 生成核销码
     * @param reservationId 预约ID
     * @return 核销码
     */
    private String generateVerificationCode(Long reservationId) {
        try {
            String input = reservationId + System.currentTimeMillis() + "campus_facility";
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().substring(0, 8).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            // 如果MD5不可用，使用简单的随机码
            return String.format("%08d", (int)(Math.random() * 100000000));
        }
    }

    @GetMapping("/stats/time-range")
    public Result<Map<String, Object>> getStatsByTimeRange(@RequestParam String range) {
        LocalDateTime startTime = getStartTimeByRange(range);
        List<Reservation> reservations = reservationRepository.findByCreatedAtAfter(startTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", reservations.size());
        result.put("reservations", reservations);
        
        return Result.success(result);
    }

    @GetMapping("/stats/category")
    public Result<Map<String, Object>> getCategoryStats(@RequestParam(required = false) String range) {
        LocalDateTime startTime = range != null ? getStartTimeByRange(range) : LocalDateTime.of(2000, 1, 1, 0, 0);
        List<Reservation> reservations = reservationRepository.findByCreatedAtAfter(startTime);
        
        Map<String, Integer> categoryCount = new LinkedHashMap<>();
        for (Reservation r : reservations) {
            Optional<Facility> facOpt = facilityRepository.findById(r.getFacilityId());
            String category = "未分类";
            if (facOpt.isPresent() && facOpt.get().getCategory() != null) {
                category = facOpt.get().getCategory();
            }
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }
        
        List<Map<String, Object>> pieData = new ArrayList<>();
        String[] colors = {"#409eff", "#67c23a", "#e6a23c", "#f56c6c", "#909399", "#c71585", "#00ced1", "#ff6347"};
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            item.put("itemStyle", Map.of("color", colors[colorIndex % colors.length]));
            pieData.add(item);
            colorIndex++;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("categoryData", pieData);
        result.put("total", reservations.size());
        
        return Result.success(result);
    }

    private LocalDateTime getStartTimeByRange(String range) {
        LocalDateTime now = LocalDateTime.now();
        return switch (range) {
            case "1d" -> now.minusDays(1);
            case "7d" -> now.minusDays(7);
            case "30d" -> now.minusDays(30);
            case "180d" -> now.minusDays(180);
            case "365d" -> now.minusDays(365);
            default -> now.minusDays(7);
        };
    }

    /**
     * 检查时间是否冲突
     * @param start1 第一个时间段的开始时间
     * @param end1 第一个时间段的结束时间
     * @param start2 第二个时间段的开始时间
     * @param end2 第二个时间段的结束时间
     * @return 是否冲突
     */
    private boolean isTimeConflict(LocalDateTime start1, LocalDateTime end1, 
                                  LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    /**
     * 批量丰富预约记录信息，添加设备名称和用户姓名
     * @param reservations 预约记录列表
     */
    private void enrichReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            enrichReservation(reservation);
        }
    }

    /**
     * 丰富单个预约记录信息，添加设备名称和用户姓名
     * @param reservation 预约记录
     */
    private void enrichReservation(Reservation reservation) {
        // 设置设施名称
        Optional<Facility> facility = facilityRepository.findById(reservation.getFacilityId());
        facility.ifPresent(e -> reservation.setFacilityName(e.getName()));

        // 设置用户名称 - 优先使用realName，不存在时使用用户名
        Optional<User> userOpt = userRepository.findById(reservation.getUserId());
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            // 优先使用realName，如果为空则使用username
            String displayName = u.getRealName();
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = u.getUsername();
            }
            reservation.setUserName(displayName);
            reservation.setUserRole(u.getRole());
        } else {
            // 用户不存在时，显示"未知用户"并记录日志
            reservation.setUserName("未知用户");
            reservation.setUserRole(null);
        }
        
        // 设置核销管理员姓名
        if (reservation.getVerifiedBy() != null) {
            Optional<User> verifiedByOpt = userRepository.findById(reservation.getVerifiedBy());
            if (verifiedByOpt.isPresent()) {
                User verifiedByUser = verifiedByOpt.get();
                String verifiedByName = verifiedByUser.getRealName();
                if (verifiedByName == null || verifiedByName.trim().isEmpty()) {
                    verifiedByName = verifiedByUser.getUsername();
                }
                reservation.setVerifiedByName(verifiedByName);
            }
        }
    }

    /**
     * 定时任务：自动标记爽约的预约
     * 每30分钟执行一次，检查已批准但未签到的预约是否已过期
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void autoMarkMissedReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查找所有已批准但未签到的预约，且结束时间已经过去超过30分钟
        List<Reservation> missedReservations = reservationRepository.findByStatusAndCheckinStatus("APPROVED", "NOT_CHECKED");
        
        int missedCount = 0;
        for (Reservation reservation : missedReservations) {
            // 如果预约结束时间已经过去超过30分钟，标记为爽约
            if (now.isAfter(reservation.getEndTime().plusMinutes(30))) {
                reservation.setCheckinStatus("MISSED");
                reservationRepository.save(reservation);
                missedCount++;
            }
        }
        
        if (missedCount > 0) {
            System.out.println("自动标记爽约预约完成，共标记 " + missedCount + " 条记录");
        }
    }
}