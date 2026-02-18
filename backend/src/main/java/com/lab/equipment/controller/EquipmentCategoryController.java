package com.lab.equipment.controller;

import com.lab.equipment.common.Result;
import com.lab.equipment.entity.EquipmentCategory;
import com.lab.equipment.repository.EquipmentCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 设备类别管理控制器
 * 提供设备类别的增删改查、状态管理等功能
 */
@RestController
@RequestMapping("/api/equipment-category")
public class EquipmentCategoryController {

    @Autowired
    private EquipmentCategoryRepository equipmentCategoryRepository;

    /**
     * 获取所有设备类别
     * @return 所有设备类别列表，按排序顺序升序排列
     */
    @GetMapping("/list")
    public Result<List<EquipmentCategory>> list() {
        List<EquipmentCategory> categories = equipmentCategoryRepository.findAllOrderBySortOrderAsc();
        return Result.success(categories);
    }

    /**
     * 获取启用的设备类别
     * @return 启用状态的设备类别列表，按排序顺序升序排列
     */
    @GetMapping("/active")
    public Result<List<EquipmentCategory>> getActiveCategories() {
        List<EquipmentCategory> categories = equipmentCategoryRepository.findByStatusOrderBySortOrderAsc("ACTIVE");
        return Result.success(categories);
    }

    /**
     * 根据ID获取设备类别
     * @param id 设备类别ID
     * @return 设备类别详情
     */
    @GetMapping("/{id}")
    public Result<EquipmentCategory> getById(@PathVariable Long id) {
        Optional<EquipmentCategory> category = equipmentCategoryRepository.findById(id);
        if (category.isPresent()) {
            return Result.success(category.get());
        }
        return Result.error("设备类别不存在");
    }

    /**
     * 创建设备类别
     * @param category 设备类别信息
     * @return 创建的设备类别信息
     */
    @PostMapping
    public Result<EquipmentCategory> create(@RequestBody EquipmentCategory category) {
        // 检查类别名称是否已存在
        if (equipmentCategoryRepository.existsByCategoryName(category.getCategoryName())) {
            return Result.error("类别名称已存在");
        }


        // 设置默认状态
        if (category.getStatus() == null || category.getStatus().isEmpty()) {
            category.setStatus("ACTIVE");
        }

        // 设置默认排序顺序
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        EquipmentCategory savedCategory = equipmentCategoryRepository.save(category);
        return Result.success("创建成功", savedCategory);
    }

    /**
     * 更新设备类别
     * @param id 设备类别ID
     * @param category 更新的设备类别信息
     * @return 更新后的设备类别信息
     */
    @PutMapping("/{id}")
    public Result<EquipmentCategory> update(@PathVariable Long id, @RequestBody EquipmentCategory category) {
        Optional<EquipmentCategory> categoryOpt = equipmentCategoryRepository.findById(id);
        if (!categoryOpt.isPresent()) {
            return Result.error("设备类别不存在");
        }

        EquipmentCategory existingCategory = categoryOpt.get();

        // 检查类别名称是否已被其他记录使用
        Optional<EquipmentCategory> existingByName = equipmentCategoryRepository.findByCategoryName(category.getCategoryName());
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            return Result.error("类别名称已存在");
        }

        // 更新字段
        existingCategory.setCategoryName(category.getCategoryName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setSortOrder(category.getSortOrder());
        existingCategory.setStatus(category.getStatus());

        EquipmentCategory savedCategory = equipmentCategoryRepository.save(existingCategory);
        return Result.success("更新成功", savedCategory);
    }

    /**
     * 删除设备类别
     * @param id 设备类别ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!equipmentCategoryRepository.existsById(id)) {
            return Result.error("设备类别不存在");
        }
        equipmentCategoryRepository.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 切换设备类别状态
     * @param id 设备类别ID
     * @return 更新状态后的设备类别信息
     */
    @PutMapping("/{id}/toggle-status")
    public Result<EquipmentCategory> toggleStatus(@PathVariable Long id) {
        Optional<EquipmentCategory> categoryOpt = equipmentCategoryRepository.findById(id);
        if (!categoryOpt.isPresent()) {
            return Result.error("设备类别不存在");
        }

        EquipmentCategory category = categoryOpt.get();
        String newStatus = "ACTIVE".equals(category.getStatus()) ? "INACTIVE" : "ACTIVE";
        category.setStatus(newStatus);

        EquipmentCategory savedCategory = equipmentCategoryRepository.save(category);
        return Result.success("状态切换成功", savedCategory);
    }

    /**
     * 搜索设备类别（模糊查询）
     */
    @GetMapping("/search")
    public Result<List<EquipmentCategory>> search(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            List<EquipmentCategory> categories = equipmentCategoryRepository.findAllOrderBySortOrderAsc();
            return Result.success(categories);
        }

        String trimmedKeyword = keyword.trim();
        List<EquipmentCategory> categories = equipmentCategoryRepository.findByKeywordContainingIgnoreCaseOrderBySortOrderAsc(trimmedKeyword);
        return Result.success(categories);
    }

    /**
     * 根据类别名称搜索
     */
    @GetMapping("/search/name")
    public Result<List<EquipmentCategory>> searchByName(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            List<EquipmentCategory> categories = equipmentCategoryRepository.findAllOrderBySortOrderAsc();
            return Result.success(categories);
        }

        String trimmedKeyword = keyword.trim();
        List<EquipmentCategory> categories = equipmentCategoryRepository.findByCategoryNameContainingIgnoreCaseOrderBySortOrderAsc(trimmedKeyword);
        return Result.success(categories);
    }

    /**
     * 分页查询所有类别
     */
    @GetMapping("/page")
    public Result<Page<EquipmentCategory>> listPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<EquipmentCategory> categoryPage = equipmentCategoryRepository.findAllByOrderBySortOrderAsc(pageable);
        return Result.success(categoryPage);
    }

    /**
     * 分页搜索设备类别（模糊查询）
     */
    @GetMapping("/search/page")
    public Result<Page<EquipmentCategory>> searchPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        if (keyword == null || keyword.trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<EquipmentCategory> categoryPage = equipmentCategoryRepository.findAllByOrderBySortOrderAsc(pageable);
            return Result.success(categoryPage);
        }

        String trimmedKeyword = keyword.trim();
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<EquipmentCategory> categoryPage = equipmentCategoryRepository.findByKeywordContainingIgnoreCaseOrderBySortOrderAsc(trimmedKeyword, pageable);
        return Result.success(categoryPage);
    }

    /**
     * 分页根据类别名称搜索
     */
    @GetMapping("/search/name/page")
    public Result<Page<EquipmentCategory>> searchByNamePage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        if (keyword == null || keyword.trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<EquipmentCategory> categoryPage = equipmentCategoryRepository.findAllByOrderBySortOrderAsc(pageable);
            return Result.success(categoryPage);
        }

        String trimmedKeyword = keyword.trim();
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<EquipmentCategory> categoryPage = equipmentCategoryRepository.findByCategoryNameContainingIgnoreCaseOrderBySortOrderAsc(trimmedKeyword, pageable);
        return Result.success(categoryPage);
    }

    /**
     * 分页根据状态查询
     */
    @GetMapping("/status/page")
    public Result<Page<EquipmentCategory>> listByStatusPage(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<EquipmentCategory> categoryPage = equipmentCategoryRepository.findByStatusOrderBySortOrderAsc(status, pageable);
        return Result.success(categoryPage);
    }
}