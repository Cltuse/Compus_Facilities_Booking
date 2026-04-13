package com.facility.booking.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户设施行为视图实体类
 * 用于映射数据库视图，快速获取用户行为聚合数据
 */
@Data
@Entity
@Table(name = "user_facility_behavior")
public class UserFacilityBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "facility_id")
    private Long facilityId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "booking_count")
    private Integer bookingCount;

    @Column(name = "last_booking_time")
    private LocalDateTime lastBookingTime;
}
