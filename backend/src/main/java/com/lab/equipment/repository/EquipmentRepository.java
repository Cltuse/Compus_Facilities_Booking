package com.lab.equipment.repository;

import com.lab.equipment.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByStatus(String status);

    List<Equipment> findByNameContaining(String name);

    List<Equipment> findByCategory(String category);

    // 分页查询所有设备
    Page<Equipment> findAll(Pageable pageable);

    // 分页搜索设备（按名称、型号、类别、位置）
    @Query("SELECT e FROM Equipment e WHERE " +
           "e.name LIKE %:keyword% OR " +
           "e.model LIKE %:keyword% OR " +
           "e.category LIKE %:keyword% OR " +
           "e.location LIKE %:keyword%")
    Page<Equipment> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 按状态分页查询
    Page<Equipment> findByStatus(String status, Pageable pageable);

    // 按类别分页查询
    Page<Equipment> findByCategory(String category, Pageable pageable);
}
