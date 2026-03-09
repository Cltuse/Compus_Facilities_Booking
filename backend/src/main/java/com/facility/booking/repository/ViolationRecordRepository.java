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
    
    // 根据用户名模糊查询（不区分大小写）
    @Query("SELECT v FROM ViolationRecord v JOIN User u ON v.userId = u.id WHERE LOWER(u.realName) LIKE LOWER(CONCAT('%', :userName, '%'))")
    Page<ViolationRecord> findByUserNameContainingIgnoreCase(@Param("userName") String userName, Pageable pageable);
    
    // 根据违规类型查询
    Page<ViolationRecord> findByViolationType(String violationType, Pageable pageable);
    
    // 根据状态查询
    Page<ViolationRecord> findByStatus(String status, Pageable pageable);
    
    // 根据用户名和违规类型查询（不区分大小写）
    @Query("SELECT v FROM ViolationRecord v JOIN User u ON v.userId = u.id WHERE LOWER(u.realName) LIKE LOWER(CONCAT('%', :userName, '%')) AND v.violationType = :violationType")
    Page<ViolationRecord> findByUserNameAndViolationType(@Param("userName") String userName, @Param("violationType") String violationType, Pageable pageable);
    
    // 根据用户名和状态查询（不区分大小写）
    @Query("SELECT v FROM ViolationRecord v JOIN User u ON v.userId = u.id WHERE LOWER(u.realName) LIKE LOWER(CONCAT('%', :userName, '%')) AND v.status = :status")
    Page<ViolationRecord> findByUserNameAndStatus(@Param("userName") String userName, @Param("status") String status, Pageable pageable);
    
    // 根据违规类型和状态查询
    Page<ViolationRecord> findByViolationTypeAndStatus(String violationType, String status, Pageable pageable);
    
    // 根据所有条件查询（不区分大小写）
    @Query("SELECT v FROM ViolationRecord v JOIN User u ON v.userId = u.id WHERE LOWER(u.realName) LIKE LOWER(CONCAT('%', :userName, '%')) AND v.violationType = :violationType AND v.status = :status")
    Page<ViolationRecord> findByFilters(@Param("userName") String userName, @Param("violationType") String violationType, @Param("status") String status, Pageable pageable);
    
    // 数据库中没有violation_time字段，使用reportedTime代替
}