package com.facility.booking.repository;

import com.facility.booking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

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
}