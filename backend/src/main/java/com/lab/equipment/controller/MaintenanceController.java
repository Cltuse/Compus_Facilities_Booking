package com.lab.equipment.controller;

import com.lab.equipment.common.Result;
import com.lab.equipment.entity.Equipment;
import com.lab.equipment.entity.Maintenance;
import com.lab.equipment.repository.EquipmentRepository;
import com.lab.equipment.repository.MaintenanceRepository;
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
    private EquipmentRepository equipmentRepository;

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
     * @param equipmentId 设备ID
     * @return 该设备的维护记录列表
     */
    @GetMapping("/equipment/{equipmentId}")
    public Result<List<Maintenance>> getByEquipmentId(@PathVariable Long equipmentId) {
        List<Maintenance> maintenances = maintenanceRepository.findByEquipmentId(equipmentId);
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
        maintenance.setId(id);
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
     * 丰富单个维护记录信息，添加设备名称
     * @param maintenance 维护记录
     */
    private void enrichMaintenance(Maintenance maintenance) {
        Optional<Equipment> equipment = equipmentRepository.findById(maintenance.getEquipmentId());
        equipment.ifPresent(e -> maintenance.setEquipmentName(e.getName()));
    }
}
