package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.entity.Facility;
import com.facility.booking.entity.Maintenance;
import com.facility.booking.entity.User;
import com.facility.booking.repository.FacilityRepository;
import com.facility.booking.repository.MaintenanceRepository;
import com.facility.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 设备维护记录控制器
 * 提供设备维护记录的增删改查等功能
 */
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;


    /**
     * 获取所有维护记录
     * @return 维护记录列表
     */
    @GetMapping("/list")
    public Result<List<Maintenance>> list() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        enrichMaintenances(maintenances);
        return Result.success(maintenances);
    }

    /**
     * 根据设备ID获取维护记录
     * @param facilityId 设备ID
     * @return 该设备的维护记录列表
     */
    @GetMapping("/facility/{facilityId}")
    public Result<List<Maintenance>> getByfacilityId(@PathVariable Long facilityId) {
        List<Maintenance> maintenances = maintenanceRepository.findByFacilityId(facilityId);
        enrichMaintenances(maintenances);
        return Result.success(maintenances);
    }

    /**
     * 根据维护人员ID获取维护记录
     * @param maintainerId 维护人员ID
     * @return 该维护人员的维护记录列表
     */
    @GetMapping("/maintainer/{maintainerId}")
    public Result<List<Maintenance>> getByMaintainerId(@PathVariable Long maintainerId) {
        List<Maintenance> maintenances = maintenanceRepository.findByMaintainerId(maintainerId);
        enrichMaintenances(maintenances);
        return Result.success(maintenances);
    }

    /**
     * 根据ID获取维护记录详情
     * @param id 维护记录ID
     * @return 维护记录详情
     */
    @GetMapping("/{id}")
    public Result<Maintenance> getById(@PathVariable Long id) {
        Optional<Maintenance> maintenance = maintenanceRepository.findById(id);
        if (maintenance.isPresent()) {
            Maintenance m = maintenance.get();
            enrichMaintenance(m);
            return Result.success(m);
        }
        return Result.error("维护记录不存在");
    }

    /**
     * 创建维护记录
     * @param maintenance 维护记录信息
     * @return 创建的维护记录信息
     */
    @PostMapping
    public Result<Maintenance> create(@RequestBody Maintenance maintenance) {
        // 验证必要字段
        if (maintenance.getFacilityId() == null) {
            return Result.error("设施ID不能为空");
        }
        if (maintenance.getMaintainerId() == null) {
            return Result.error("维护人员ID不能为空");
        }
        if (maintenance.getMaintenanceType() == null || maintenance.getMaintenanceType().trim().isEmpty()) {
            return Result.error("维护类型不能为空");
        }
        if (maintenance.getDescription() == null || maintenance.getDescription().trim().isEmpty()) {
            return Result.error("维护描述不能为空");
        }
        
        // 验证时间逻辑
        if (maintenance.getStartTime() != null && maintenance.getEndTime() != null) {
            if (maintenance.getEndTime().isBefore(maintenance.getStartTime())) {
                return Result.error("结束时间不能早于开始时间");
            }
        }
        
        // 设置默认状态
        if (maintenance.getStatus() == null || maintenance.getStatus().trim().isEmpty()) {
            maintenance.setStatus("PENDING");
        }
        
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        enrichMaintenance(savedMaintenance);
        return Result.success("创建成功", savedMaintenance);
    }

    /**
     * 更新维护记录
     * @param id 维护记录ID
     * @param maintenance 更新的维护记录信息
     * @return 更新后的维护记录信息
     */
    @PutMapping("/{id}")
    public Result<Maintenance> update(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        if (!maintenanceRepository.existsById(id)) {
            return Result.error("维护记录不存在");
        }
        
        // 验证时间逻辑
        if (maintenance.getStartTime() != null && maintenance.getEndTime() != null) {
            if (maintenance.getEndTime().isBefore(maintenance.getStartTime())) {
                return Result.error("结束时间不能早于开始时间");
            }
        }
        
        // 获取现有记录，确保不丢失数据
        Optional<Maintenance> existingOpt = maintenanceRepository.findById(id);
        if (existingOpt.isPresent()) {
            Maintenance existing = existingOpt.get();
            
            // 如果前端没有传递某些字段，保持原有值
            if (maintenance.getFacilityId() == null) {
                maintenance.setFacilityId(existing.getFacilityId());
            }
            if (maintenance.getMaintainerId() == null) {
                maintenance.setMaintainerId(existing.getMaintainerId());
            }
            if (maintenance.getMaintenanceType() == null) {
                maintenance.setMaintenanceType(existing.getMaintenanceType());
            }
            if (maintenance.getDescription() == null) {
                maintenance.setDescription(existing.getDescription());
            }
            if (maintenance.getMaintainer() == null) {
                maintenance.setMaintainer(existing.getMaintainer());
            }
            if (maintenance.getStatus() == null || maintenance.getStatus().trim().isEmpty()) {
                maintenance.setStatus(existing.getStatus());
            }
        }
        
        maintenance.setId(id);
        
        // 如果提供了maintainerId但没有maintainer姓名，自动填充
        if (maintenance.getMaintainerId() != null && (maintenance.getMaintainer() == null || maintenance.getMaintainer().trim().isEmpty())) {
            Optional<User> maintainer = userRepository.findById(maintenance.getMaintainerId());
            if (maintainer.isPresent()) {
                User user = maintainer.get();
                // 优先使用真实姓名，如果没有则使用用户名
                String maintainerName = user.getRealName() != null && !user.getRealName().trim().isEmpty() 
                    ? user.getRealName() : user.getUsername();
                maintenance.setMaintainer(maintainerName);
            }
        }
        
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        enrichMaintenance(savedMaintenance);
        return Result.success("更新成功", savedMaintenance);
    }

    /**
     * 删除维护记录
     * @param id 维护记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!maintenanceRepository.existsById(id)) {
            return Result.error("维护记录不存在");
        }
        maintenanceRepository.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量丰富维护记录信息，添加设备名称
     * @param maintenances 维护记录列表
     */
    private void enrichMaintenances(List<Maintenance> maintenances) {
        for (Maintenance maintenance : maintenances) {
            enrichMaintenance(maintenance);
        }
    }

    /**
     * 丰富单个维护记录信息，添加设备名称和维护人员姓名
     * @param maintenance 维护记录
     */
    private void enrichMaintenance(Maintenance maintenance) {
        // 获取设施名称
        Optional<Facility> facility = facilityRepository.findById(maintenance.getFacilityId());
        facility.ifPresent(e -> maintenance.setFacilityName(e.getName()));
        
        // 如果提供了maintainerId但没有maintainer姓名，自动填充
        if (maintenance.getMaintainerId() != null && (maintenance.getMaintainer() == null || maintenance.getMaintainer().trim().isEmpty())) {
            Optional<User> maintainer = userRepository.findById(maintenance.getMaintainerId());
            if (maintainer.isPresent()) {
                User user = maintainer.get();
                // 优先使用真实姓名，如果没有则使用用户名
                String maintainerName = user.getRealName() != null && !user.getRealName().trim().isEmpty() 
                    ? user.getRealName() : user.getUsername();
                maintenance.setMaintainer(maintainerName);
            }
        }
    }
}