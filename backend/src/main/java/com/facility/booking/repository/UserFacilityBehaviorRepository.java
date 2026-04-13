package com.facility.booking.repository;

import com.facility.booking.entity.UserFacilityBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户设施行为视图Repository接口
 */
@Repository
public interface UserFacilityBehaviorRepository extends JpaRepository<UserFacilityBehavior, Long> {

    /**
     * 根据用户ID查找用户行为数据
     * @param userId 用户ID
     * @return 用户行为列表
     */
    List<UserFacilityBehavior> findByUserId(Long userId);

    /**
     * 根据设施ID查找用户行为数据
     * @param facilityId 设施ID
     * @return 用户行为列表
     */
    List<UserFacilityBehavior> findByFacilityId(Long facilityId);

    /**
     * 根据用户ID和设施ID查找行为数据
     * @param userId 用户ID
     * @param facilityId 设施ID
     * @return 用户行为数据
     */
    UserFacilityBehavior findByUserIdAndFacilityId(Long userId, Long facilityId);

    /**
     * 获取所有用户ID列表（去重）
     * @return 用户ID列表
     */
    @Query("SELECT DISTINCT u.userId FROM UserFacilityBehavior u")
    List<Long> findAllUserIds();

    /**
     * 获取所有设施ID列表（去重）
     * @return 设施ID列表
     */
    @Query("SELECT DISTINCT u.facilityId FROM UserFacilityBehavior u")
    List<Long> findAllFacilityIds();

    /**
     * 根据用户ID和类别名称查找行为数据
     * @param userId 用户ID
     * @param categoryName 类别名称
     * @return 用户行为列表
     */
    List<UserFacilityBehavior> findByUserIdAndCategoryName(Long userId, String categoryName);
}
