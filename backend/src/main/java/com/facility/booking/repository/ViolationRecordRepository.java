package com.facility.booking.repository;

import com.facility.booking.entity.ViolationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ViolationRecordRepository extends JpaRepository<ViolationRecord, Long> {
    
    Page<ViolationRecord> findByUserIdOrderByReportedTimeDesc(Long userId, Pageable pageable);
    
    // 数据库中没有violation_time字段，使用reportedTime代替
    
    Page<ViolationRecord> findByUserId(Long userId, Pageable pageable);
    
    Page<ViolationRecord> findByUserIdAndStatusOrderByReportedTimeDesc(Long userId, String status, Pageable pageable);
    
    // 数据库中没有violation_time字段，使用reportedTime代替


    @Query("SELECT v FROM ViolationRecord v WHERE v.userId = :userId AND v.reportedTime BETWEEN :startTime AND :endTime")
    Page<ViolationRecord> findByUserIdAndTimeRange(@Param("userId") Long userId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime, 
                                                   Pageable pageable);
    
    // 数据库中没有violation_time字段，使用reportedTime代替
    
    @Query("SELECT COUNT(v) FROM ViolationRecord v WHERE v.userId = :userId AND v.status = 'PENDING'")
    Long countActiveViolationsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(v.penaltyPoints) FROM ViolationRecord v WHERE v.userId = :userId")
    Integer sumPenaltyPointsByUserId(@Param("userId") Long userId);
    
    List<ViolationRecord> findByUserIdAndStatusOrderByReportedTimeDesc(Long userId, String status);
    
    @Query("SELECT v FROM ViolationRecord v WHERE v.userId = :userId AND v.status = 'PENDING'")
    List<ViolationRecord> findPendingViolationsByUserId(@Param("userId") Long userId);
    
    Page<ViolationRecord> findAllByOrderByReportedTimeDesc(Pageable pageable);
    
    // 数据库中没有violation_time字段，使用reportedTime代替
}