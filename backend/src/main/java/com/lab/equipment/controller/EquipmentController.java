package com.lab.equipment.controller;

import com.lab.equipment.common.Result;
import com.lab.equipment.entity.Equipment;
import com.lab.equipment.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 设备控制器
 * 提供设备管理的相关接口，包括设备的增删改查、状态管理等功能
 */
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentRepository equipmentRepository;

    /**
     * 获取所有设备列表
     * @return 设备列表
     */
    @GetMapping("/list")
    public Result<List<Equipment>> list() {
        List<Equipment> equipments = equipmentRepository.findAll();
        return Result.success(equipments);
    }

    /**
     * 获取所有可用设备
     * @return 可用设备列表
     */
    @GetMapping("/available")
    public Result<List<Equipment>> getAvailable() {
        List<Equipment> equipments = equipmentRepository.findByStatus("AVAILABLE");
        return Result.success(equipments);
    }

    /**
     * 根据ID获取设备详情
     * @param id 设备ID
     * @return 设备详情
     */
    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Long id) {
        Optional<Equipment> equipment = equipmentRepository.findById(id);
        if (equipment.isPresent()) {
            return Result.success(equipment.get());
        }
        return Result.error("设备不存在");
    }

    /**
     * 搜索设备（根据设备名称）
     * @param keyword 搜索关键词
     * @return 匹配的设备列表
     */
    @GetMapping("/search")
    public Result<List<Equipment>> search(@RequestParam String keyword) {
        List<Equipment> equipments = equipmentRepository.findByNameContaining(keyword);
        return Result.success(equipments);
    }

    /**
     * 分页获取设备列表
     * @param page 页码，从0开始
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向（asc/desc）
     * @return 分页数据
     */
    @GetMapping("/listPage")
    public Result<Map<String, Object>> listPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Equipment> equipmentPage = equipmentRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", equipmentPage.getContent());
        response.put("totalElements", equipmentPage.getTotalElements());
        response.put("totalPages", equipmentPage.getTotalPages());
        response.put("size", equipmentPage.getSize());
        response.put("number", equipmentPage.getNumber());
        response.put("first", equipmentPage.isFirst());
        response.put("last", equipmentPage.isLast());

        return Result.success(response);
    }

    /**
     * 分页搜索设备
     * @param keyword 搜索关键词
     * @param page 页码，从0开始
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向（asc/desc）
     * @return 搜索结果的分页数据
     */
    @GetMapping("/searchPage")
    public Result<Map<String, Object>> searchPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Equipment> equipmentPage = equipmentRepository.searchByKeyword(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", equipmentPage.getContent());
        response.put("totalElements", equipmentPage.getTotalElements());
        response.put("totalPages", equipmentPage.getTotalPages());
        response.put("size", equipmentPage.getSize());
        response.put("number", equipmentPage.getNumber());
        response.put("first", equipmentPage.isFirst());
        response.put("last", equipmentPage.isLast());

        return Result.success(response);
    }

    /**
     * 创建新设备
     * @param equipment 设备信息
     * @return 创建的设备信息
     */
    @PostMapping
    public Result<Equipment> create(@RequestBody Equipment equipment) {
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return Result.success("创建成功", savedEquipment);
    }

    /**
     * 更新设备信息
     * @param id 设备ID
     * @param equipment 更新的设备信息
     * @return 更新后的设备信息
     */
    @PutMapping("/{id}")
    public Result<Equipment> update(@PathVariable Long id, @RequestBody Equipment equipment) {
        if (!equipmentRepository.existsById(id)) {
            return Result.error("设备不存在");
        }
        equipment.setId(id);
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return Result.success("更新成功", savedEquipment);
    }

    /**
     * 更新设备状态
     * @param id 设备ID
     * @param requestBody 包含新状态的请求体
     * @return 更新后的设备信息
     */
    @PutMapping("/{id}/status")
    public Result<Equipment> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(id);
        if (!equipmentOpt.isPresent()) {
            return Result.error("设备不存在");
        }

        Equipment equipment = equipmentOpt.get();
        String status = requestBody.get("status");
        if (status == null || status.trim().isEmpty()) {
            return Result.error("状态不能为空");
        }

        equipment.setStatus(status.trim().toUpperCase());
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return Result.success("设备状态更新成功", savedEquipment);
    }

    /**
     * 删除设备
     * @param id 设备ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!equipmentRepository.existsById(id)) {
            return Result.error("设备不存在");
        }
        equipmentRepository.deleteById(id);
        return Result.success("删除成功", null);
    }
}
