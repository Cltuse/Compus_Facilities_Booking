package com.facility.booking.repository;

import com.facility.booking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByFacilityId(Long facilityId);
    List<Reservation> findByStatus(String status);

    @Query("SELECT r FROM Reservation r WHERE r.createdAt >= :startTime")
    List<Reservation> findByCreatedAtAfter(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT r FROM Reservation r WHERE r.createdAt >= :startTime AND r.facilityId IN :facilityIds")
    List<Reservation> findByCreatedAtAfterAndFacilityIdIn(@Param("startTime") LocalDateTime startTime, @Param("facilityIds") List<Long> facilityIds);

    // 自定义查询方法用于搜索
    @Query("SELECT r FROM Reservation r WHERE " +
            "LOWER(r.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "EXISTS(SELECT u FROM User u WHERE u.id = r.userId AND " +
            "(LOWER(u.realName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')))) OR " +
            "EXISTS(SELECT f FROM Facility f WHERE f.id = r.facilityId AND " +
            "LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Reservation> findByKeyword(@Param("keyword") String keyword);
    
    // 签到签退相关查询
    List<Reservation> findByCheckinStatus(String checkinStatus);
    List<Reservation> findByStatusAndCheckinStatus(String status, String checkinStatus);
    List<Reservation> findByCheckinStatusAndStartTimeBetween(String checkinStatus, LocalDateTime startTime, LocalDateTime endTime);
    List<Reservation> findByCheckinStatusAndEndTimeBefore(String checkinStatus, LocalDateTime endTime);
    
    // 根据用户ID和状态列表查询预约
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.status IN :statuses")
    List<Reservation> findByUserIdAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<String> statuses);

    // 根据核销码查询预约
    @Query("SELECT r FROM Reservation r WHERE r.verificationCode = :verificationCode")
    Optional<Reservation> findByVerificationCode(@Param("verificationCode") String verificationCode);

    }