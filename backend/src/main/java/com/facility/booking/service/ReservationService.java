package com.facility.booking.service;

import com.facility.booking.entity.Facility;
import com.facility.booking.entity.Reservation;
import com.facility.booking.entity.User;
import com.facility.booking.repository.FacilityRepository;
import com.facility.booking.repository.ReservationRepository;
import com.facility.booking.repository.UserRepository;
import com.facility.booking.repository.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BlacklistRepository blacklistRepository;

    /**
     * 验证预约创建的有效性
     */
    public String validateReservationCreation(Reservation reservation) {
        // 1. 基本数据验证
        if (reservation.getFacilityId() == null) {
            return "设施ID不能为空";
        }
        if (reservation.getUserId() == null) {
            return "用户ID不能为空";
        }
        if (reservation.getStartTime() == null) {
            return "开始时间不能为空";
        }
        if (reservation.getEndTime() == null) {
            return "结束时间不能为空";
        }
        
        // 2. 检查设施是否存在
        Optional<Facility> facilityOpt = facilityRepository.findById(reservation.getFacilityId());
        if (!facilityOpt.isPresent()) {
            return "设施不存在";
        }
        
        // 设施状态不影响预约，通过时间冲突检查来控制预约有效性
        // 设施状态仅有 AVAILABLE, MAINTENANCE, DAMAGED 三种
        
        // 3. 检查用户是否存在
        Optional<User> userOpt = userRepository.findById(reservation.getUserId());
        if (!userOpt.isPresent()) {
            return "用户不存在";
        }
        
        // 4. 检查用户是否在黑名单中（查询当前有效的黑名单记录）
        if (blacklistRepository.findByUserIdAndStatus(reservation.getUserId(), "ACTIVE").isPresent()) {
            return "该用户已被列入黑名单，无法预约";
        }
        
        // 5. 检查时间有效性
        if (reservation.getEndTime().isBefore(reservation.getStartTime())) {
            return "结束时间不能早于开始时间";
        }
        
        // 6. 检查预约时间范围（不能预约过去的时间）
        if (reservation.getStartTime().isBefore(LocalDateTime.now())) {
            return "不能预约过去的时间";
        }
        
        // 7. 检查预约时长限制（最长预约时间不超过24小时）
        long durationHours = java.time.Duration.between(reservation.getStartTime(), reservation.getEndTime()).toHours();
        if (durationHours > 24) {
            return "单次预约时长不能超过24小时";
        }
        
        // 8. 检查时间冲突
        List<Reservation> existingReservations = reservationRepository.findByFacilityId(reservation.getFacilityId());
        for (Reservation existing : existingReservations) {
            if (isTimeConflict(reservation.getStartTime(), reservation.getEndTime(), 
                             existing.getStartTime(), existing.getEndTime()) &&
                !"REJECTED".equals(existing.getStatus()) && 
                !"CANCELLED".equals(existing.getStatus())) {
                return "该时间段已被预约，请选择其他时间";
            }
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证用户签到操作
     */
    public String validateCheckin(Reservation reservation, Long userId) {
        // 验证用户身份：只有预约用户本人才能签到
        if (!reservation.getUserId().equals(userId)) {
            return "只有预约用户本人才能进行签到操作";
        }
        
        // 检查预约状态
        if (!"APPROVED".equals(reservation.getStatus())) {
            return "只有已通过的预约才能签到";
        }
        
        // 检查签到状态
        if (!"NOT_CHECKED".equals(reservation.getCheckinStatus())) {
            if ("CHECKED_IN".equals(reservation.getCheckinStatus())) {
                return "该预约已签到，请勿重复操作";
            } else if ("CHECKED_OUT".equals(reservation.getCheckinStatus())) {
                return "该预约已完成签退，无法再次签到";
            } else {
                return "该预约签到状态异常";
            }
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 检查是否在预约时间范围内（可提前15分钟签到）
        if (now.isBefore(reservation.getStartTime().minusMinutes(15))) {
            return "还未到签到时间，可提前15分钟签到";
        }
        
        if (now.isAfter(reservation.getEndTime())) {
            return "预约时间已结束，无法签到";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 验证用户签退操作
     */
    public String validateCheckout(Reservation reservation, Long userId) {
        // 验证用户身份：只有预约用户本人才能签退
        if (!reservation.getUserId().equals(userId)) {
            return "只有预约用户本人才能进行签退操作";
        }
        
        // 检查预约状态
        if (!"APPROVED".equals(reservation.getStatus())) {
            return "只有已通过的预约才能签退";
        }
        
        // 检查签到状态
        if (!"CHECKED_IN".equals(reservation.getCheckinStatus())) {
            return "请先签到后再签退";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 检查时间是否冲突
     */
    private boolean isTimeConflict(LocalDateTime start1, LocalDateTime end1, 
                                  LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }
}