package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.entity.Facility;
import com.facility.booking.repository.FacilityRepository;
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
@RequestMapping("/api/facility")
public class FacilityController {

    @Autowired
    private FacilityRepository facilityRepository;

    /**
     * 获取所有设备列表
     * @return 设备列表
     */
    @GetMapping("/list")
    public Result<List<Facility>> list() {
        List<Facility> facilities = facilityRepository.findAll();
        return Result.success(facilities);
    }

    /**
     * 获取所有可用设备
     * @return 可用设备列表
     */
    @GetMapping("/available")
    public Result<List<Facility>> getAvailable() {
        List<Facility> facilities = facilityRepository.findByStatus("AVAILABLE");
        return Result.success(facilities);
    }

    /**
     * 根据ID获取设备详情
     * @param id 设备ID
     * @return 设备详情
     */
    @GetMapping("/{id}")
    public Result<Facility> getById(@PathVariable Long id) {
        Optional<Facility> facility = facilityRepository.findById(id);
        if (facility.isPresent()) {
            return Result.success(facility.get());
        }
        return Result.error("设备不存在");
    }

    /**
     * 搜索设备（根据设备名称）
     * @param keyword 搜索关键词
     * @return 匹配的设备列表
     */
    @GetMapping("/search")
    public Result<List<Facility>> search(@RequestParam String keyword) {
        List<Facility> facilities = facilityRepository.findByNameContaining(keyword);
        return Result.success(facilities);
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
        Page<Facility> facilityPage = facilityRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", facilityPage.getContent());
        response.put("totalElements", facilityPage.getTotalElements());
        response.put("totalPages", facilityPage.getTotalPages());
        response.put("size", facilityPage.getSize());
        response.put("number", facilityPage.getNumber());
        response.put("first", facilityPage.isFirst());
        response.put("last", facilityPage.isLast());

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
        Page<Facility> facilityPage = facilityRepository.searchByKeyword(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", facilityPage.getContent());
        response.put("totalElements", facilityPage.getTotalElements());
        response.put("totalPages", facilityPage.getTotalPages());
        response.put("size", facilityPage.getSize());
        response.put("number", facilityPage.getNumber());
        response.put("first", facilityPage.isFirst());
        response.put("last", facilityPage.isLast());

        return Result.success(response);
    }

    /**
     * 创建新设备
     * @param facility 设备信息
     * @return 创建的设备信息
     */
    @PostMapping
    public Result<Facility> create(@RequestBody Facility facility) {
        Facility savedfacility = facilityRepository.save(facility);
        return Result.success("创建成功", savedfacility);
    }

    /**
     * 更新设备信息
     * @param id 设备ID
     * @param facility 更新的设备信息
     * @return 更新后的设备信息
     */
    @PutMapping("/{id}")
    public Result<Facility> update(@PathVariable Long id, @RequestBody Facility facility) {
        if (!facilityRepository.existsById(id)) {
            return Result.error("设备不存在");
        }
        facility.setId(id);
        Facility savedfacility = facilityRepository.save(facility);
        return Result.success("更新成功", savedfacility);
    }

    /**
     * 更新设备状态
     * @param id 设备ID
     * @param requestBody 包含新状态的请求体
     * @return 更新后的设备信息
     */
    @PutMapping("/{id}/status")
    public Result<Facility> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        Optional<Facility> facilityOpt = facilityRepository.findById(id);
        if (!facilityOpt.isPresent()) {
            return Result.error("设备不存在");
        }

        Facility facility = facilityOpt.get();
        String status = requestBody.get("status");
        if (status == null || status.trim().isEmpty()) {
            return Result.error("状态不能为空");
        }

        facility.setStatus(status.trim().toUpperCase());
        Facility savedfacility = facilityRepository.save(facility);
        return Result.success("设备状态更新成功", savedfacility);
    }

    /**
     * 删除设备
     * @param id 设备ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!facilityRepository.existsById(id)) {
            return Result.error("设备不存在");
        }
        facilityRepository.deleteById(id);
        return Result.success("删除成功", null);
    }
}
