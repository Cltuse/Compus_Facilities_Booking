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
    
    Page<ViolationRecord> findByUserIdOrderByViolationTimeDesc(Long userId, Pageable pageable);
    
    Page<ViolationRecord> findByUserId(Long userId, Pageable pageable);
    
    Page<ViolationRecord> findByUserIdAndStatusOrderByViolationTimeDesc(Long userId, String status, Pageable pageable);
    
    @Query("SELECT v FROM ViolationRecord v WHERE v.userId = :userId AND v.violationTime BETWEEN :startTime AND :endTime")
    Page<ViolationRecord> findByUserIdAndTimeRange(@Param("userId") Long userId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime, 
                                                   Pageable pageable);
    
    @Query("SELECT COUNT(v) FROM ViolationRecord v WHERE v.userId = :userId AND v.status = 'ACTIVE'")
    Long countActiveViolationsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(v.creditDeduction) FROM ViolationRecord v WHERE v.userId = :userId AND v.status = 'ACTIVE'")
    Integer sumActiveCreditDeductionByUserId(@Param("userId") Long userId);
    
    List<ViolationRecord> findByUserIdAndStatusOrderByViolationTimeDesc(Long userId, String status);
    
    @Query("SELECT v FROM ViolationRecord v WHERE v.expireTime < :now AND v.status = 'ACTIVE'")
    List<ViolationRecord> findExpiredViolations(@Param("now") LocalDateTime now);
}