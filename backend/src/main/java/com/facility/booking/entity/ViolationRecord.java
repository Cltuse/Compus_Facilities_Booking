package com.facility.booking.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "violation_record")
public class ViolationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private Long reservationId;

    @Column(nullable = false, length = 50)
    private String violationType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "penalty_points")
    private Integer penaltyPoints = 0;

    @Column(length = 20)
    private String status = "PENDING"; // PENDING-待处理/PROCESSED-已处理

    @Column(name = "reported_by")
    private Long reportedBy;

    @Column(name = "reported_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime reportedTime;
    
    public void setReportedTime(String reportedTimeStr) {
        if (reportedTimeStr != null) {
            try {
                // Try to parse ISO format first (with timezone)
                if (reportedTimeStr.contains("T") && reportedTimeStr.contains("Z")) {
                    // Remove the 'Z' and replace 'T' with space
                    String cleanedDate = reportedTimeStr.replace("Z", "").replace("T", " ");
                    // Parse the date string
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                    this.reportedTime = LocalDateTime.parse(cleanedDate, formatter);
                } else if (reportedTimeStr.contains("T")) {
                    // Handle ISO format without Z
                    String cleanedDate = reportedTimeStr.replace("T", " ");
                    if (cleanedDate.contains(".")) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                        this.reportedTime = LocalDateTime.parse(cleanedDate, formatter);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        this.reportedTime = LocalDateTime.parse(cleanedDate, formatter);
                    }
                } else {
                    // Handle standard format
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    this.reportedTime = LocalDateTime.parse(reportedTimeStr, formatter);
                }
            } catch (Exception e) {
                // If parsing fails, set to current time
                this.reportedTime = LocalDateTime.now();
            }
        }
    }

    @Basic
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @Basic
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @Transient
    private String userName;

    @Transient
    private String facilityName;



    @Transient
    private String reporterName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}