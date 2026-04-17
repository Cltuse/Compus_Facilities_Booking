package com.facility.booking.controller;

import com.facility.booking.annotation.OperationLog;
import com.facility.booking.common.Result;
import com.facility.booking.entity.Facility;
import com.facility.booking.entity.Maintenance;
import com.facility.booking.entity.User;
import com.facility.booking.repository.FacilityRepository;
import com.facility.booking.repository.MaintenanceRepository;
import com.facility.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            Maintenance current = maintenance.get();
            enrichMaintenance(current);
            return Result.success(current);
        }
        return Result.error("维护记录不存在");
    }

    /**
     * 创建维护记录
     * @param maintenance 维护记录信息
     * @return 创建的维护记录信息
     */
    @PostMapping
    @OperationLog(operationType = "CREATE_MAINTENANCE", detail = "创建维护任务")
    public Result<Maintenance> create(@RequestBody Maintenance maintenance) {
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
        if (maintenance.getStartTime() != null && maintenance.getEndTime() != null
                && maintenance.getEndTime().isBefore(maintenance.getStartTime())) {
            return Result.error("结束时间不能早于开始时间");
        }

        if (maintenance.getStatus() == null || maintenance.getStatus().trim().isEmpty()) {
            maintenance.setStatus("PENDING");
        }
        fillMaintainerName(maintenance);

        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        syncFacilityStatus(savedMaintenance, null);
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
    @OperationLog(operationType = "UPDATE_MAINTENANCE", detail = "更新维护任务")
    public Result<Maintenance> update(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        Optional<Maintenance> existingOpt = maintenanceRepository.findById(id);
        if (!existingOpt.isPresent()) {
            return Result.error("维护记录不存在");
        }

        Maintenance existing = existingOpt.get();
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
        if (maintenance.getStartTime() == null) {
            maintenance.setStartTime(existing.getStartTime());
        }
        if (maintenance.getEndTime() == null) {
            maintenance.setEndTime(existing.getEndTime());
        }
        if (maintenance.getResult() == null) {
            maintenance.setResult(existing.getResult());
        }
        if (maintenance.getCost() == null) {
            maintenance.setCost(existing.getCost());
        }

        if (maintenance.getEndTime() != null && maintenance.getStartTime() != null
                && maintenance.getEndTime().isBefore(maintenance.getStartTime())) {
            return Result.error("结束时间不能早于开始时间");
        }

        maintenance.setId(id);
        fillMaintainerName(maintenance);

        String oldStatus = existing.getStatus();
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        syncFacilityStatus(savedMaintenance, oldStatus);
        enrichMaintenance(savedMaintenance);
        return Result.success("更新成功", savedMaintenance);
    }

    /**
     * 完成维护任务
     * @param id 维护记录ID
     * @param maintenance 维护结果信息
     * @return 完成后的维护记录
     */
    @PutMapping("/{id}/complete")
    @OperationLog(operationType = "COMPLETE_MAINTENANCE", detail = "完成维护")
    public Result<Maintenance> complete(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        Optional<Maintenance> existingOpt = maintenanceRepository.findById(id);
        if (!existingOpt.isPresent()) {
            return Result.error("维护记录不存在");
        }

        Maintenance existing = existingOpt.get();
        if ("COMPLETED".equals(existing.getStatus())) {
            return Result.error("维护任务已完成");
        }

        LocalDateTime endTime = maintenance.getEndTime() != null ? maintenance.getEndTime() : LocalDateTime.now();
        if (existing.getStartTime() != null && endTime.isBefore(existing.getStartTime())) {
            return Result.error("结束时间不能早于开始时间");
        }

        existing.setStatus("COMPLETED");
        existing.setEndTime(endTime);
        if (maintenance.getResult() != null) {
            existing.setResult(maintenance.getResult());
        }
        if (maintenance.getCost() != null) {
            existing.setCost(maintenance.getCost());
        }

        Maintenance savedMaintenance = maintenanceRepository.save(existing);
        Optional<Facility> facilityOpt = facilityRepository.findById(existing.getFacilityId());
        if (facilityOpt.isPresent()) {
            Facility facility = facilityOpt.get();
            if ("MAINTENANCE".equals(facility.getStatus())) {
                facility.setStatus("AVAILABLE");
                facilityRepository.save(facility);
            }
        }

        enrichMaintenance(savedMaintenance);
        return Result.success("维护任务已完成", savedMaintenance);
    }

    /**
     * 删除维护记录
     * @param id 维护记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @OperationLog(operationType = "DELETE_MAINTENANCE", detail = "删除维护任务")
    public Result<Void> delete(@PathVariable Long id) {
        if (!maintenanceRepository.existsById(id)) {
            return Result.error("维护记录不存在");
        }
        maintenanceRepository.deleteById(id);
        return Result.success("删除成功", null);
    }

    private void enrichMaintenances(List<Maintenance> maintenances) {
        for (Maintenance maintenance : maintenances) {
            enrichMaintenance(maintenance);
        }
    }

    private void enrichMaintenance(Maintenance maintenance) {
        Optional<Facility> facility = facilityRepository.findById(maintenance.getFacilityId());
        facility.ifPresent(value -> maintenance.setFacilityName(value.getName()));
        fillMaintainerName(maintenance);
    }

    private void fillMaintainerName(Maintenance maintenance) {
        if (maintenance.getMaintainerId() == null) {
            return;
        }
        if (maintenance.getMaintainer() != null && !maintenance.getMaintainer().trim().isEmpty()) {
            return;
        }
        Optional<User> maintainer = userRepository.findById(maintenance.getMaintainerId());
        if (maintainer.isPresent()) {
            User user = maintainer.get();
            String maintainerName = user.getRealName() != null && !user.getRealName().trim().isEmpty()
                    ? user.getRealName() : user.getUsername();
            maintenance.setMaintainer(maintainerName);
        }
    }

    private LocalDateTime getStartTimeByRange(String range) {
        LocalDateTime now = LocalDateTime.now();
        switch (range) {
            case "1d":
                return now.minusDays(1);
            case "7d":
                return now.minusDays(7);
            case "30d":
                return now.minusDays(30);
            case "180d":
                return now.minusDays(180);
            case "365d":
                return now.minusDays(365);
            default:
                return now.minusDays(7);
        }
    }

    /**
     * 获取时间段内的维修统计数据
     * @param range 时间范围
     * @return 统计数据
     */
    @GetMapping("/stats/time-range")
    public Result<Map<String, Object>> getStatsByTimeRange(@RequestParam String range) {
        LocalDateTime startTime = getStartTimeByRange(range);
        List<Maintenance> maintenances = maintenanceRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(startTime))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", maintenances.size());
        result.put("maintenances", maintenances);
        return Result.success(result);
    }

    /**
     * 获取维修类型分布统计
     * @param range 时间范围（可选）
     * @return 类型分布数据
     */
    @GetMapping("/stats/type-distribution")
    public Result<Map<String, Object>> getTypeDistribution(@RequestParam(required = false) String range) {
        LocalDateTime startTime = range != null ? getStartTimeByRange(range) : LocalDateTime.of(2000, 1, 1, 0, 0);
        List<Maintenance> maintenances = maintenanceRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(startTime))
                .collect(Collectors.toList());

        Map<String, Integer> typeCount = new LinkedHashMap<>();
        typeCount.put("ROUTINE", 0);
        typeCount.put("REPAIR", 0);
        typeCount.put("UPGRADE", 0);
        typeCount.put("OTHER", 0);

        for (Maintenance m : maintenances) {
            String type = m.getMaintenanceType();
            if (type == null) {
                typeCount.put("OTHER", typeCount.getOrDefault("OTHER", 0) + 1);
            } else if (typeCount.containsKey(type)) {
                typeCount.put(type, typeCount.get(type) + 1);
            } else {
                typeCount.put("OTHER", typeCount.getOrDefault("OTHER", 0) + 1);
            }
        }

        List<Map<String, Object>> pieData = new ArrayList<>();
        String[] colors = {"#409eff", "#67c23a", "#e6a23c", "#909399"};
        String[] typeNames = {"日常保养", "故障维修", "设备升级", "其他"};
        int index = 0;
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            if (entry.getValue() > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", typeNames[index]);
                item.put("value", entry.getValue());
                item.put("itemStyle", Map.of("color", colors[index]));
                pieData.add(item);
            }
            index++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("typeDistribution", pieData);
        return Result.success(result);
    }

    /**
     * 获取平均维修时长统计
     * @param range 时间范围（可选）
     * @return 平均时长数据
     */
    @GetMapping("/stats/duration")
    public Result<Map<String, Object>> getDurationStats(@RequestParam(required = false) String range) {
        LocalDateTime startTime = range != null ? getStartTimeByRange(range) : LocalDateTime.of(2000, 1, 1, 0, 0);
        List<Maintenance> completedMaintenances = maintenanceRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(startTime))
                .filter(m -> "COMPLETED".equals(m.getStatus()))
                .filter(m -> m.getStartTime() != null && m.getEndTime() != null)
                .collect(Collectors.toList());

        Map<String, List<Long>> durationByType = new LinkedHashMap<>();
        durationByType.put("ROUTINE", new ArrayList<>());
        durationByType.put("REPAIR", new ArrayList<>());
        durationByType.put("UPGRADE", new ArrayList<>());
        durationByType.put("OTHER", new ArrayList<>());

        for (Maintenance m : completedMaintenances) {
            long hours = Duration.between(m.getStartTime(), m.getEndTime()).toHours();
            String type = m.getMaintenanceType();
            if (type == null || !durationByType.containsKey(type)) {
                type = "OTHER";
            }
            durationByType.get(type).add(hours);
        }

        String[] typeNames = {"日常保养", "故障维修", "设备升级", "其他"};
        String[] types = {"ROUTINE", "REPAIR", "UPGRADE", "OTHER"};
        List<Map<String, Object>> barData = new ArrayList<>();

        for (int i = 0; i < types.length; i++) {
            List<Long> durations = durationByType.get(types[i]);
            double avgDuration = 0;
            if (!durations.isEmpty()) {
                avgDuration = durations.stream().mapToLong(Long::longValue).average().orElse(0);
                BigDecimal bd = BigDecimal.valueOf(avgDuration).setScale(1, RoundingMode.HALF_UP);
                avgDuration = bd.doubleValue();
            }

            Map<String, Object> item = new HashMap<>();
            item.put("type", typeNames[i]);
            item.put("avgDuration", avgDuration);
            barData.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("durationData", barData);
        return Result.success(result);
    }

    /**
     * 获取设施故障率排行
     * @param range 时间范围（可选）
     * @return 故障排行数据
     */
    @GetMapping("/stats/facility-faults")
    public Result<Map<String, Object>> getFacilityFaultStats(@RequestParam(required = false) String range) {
        LocalDateTime startTime = range != null ? getStartTimeByRange(range) : LocalDateTime.of(2000, 1, 1, 0, 0);
        List<Maintenance> maintenances = maintenanceRepository.findAll().stream()
                .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().isAfter(startTime))
                .collect(Collectors.toList());

        Map<Long, Integer> faultCountByFacility = new HashMap<>();
        for (Maintenance m : maintenances) {
            faultCountByFacility.merge(m.getFacilityId(), 1, Integer::sum);
        }

        List<Map<String, Object>> topFaults = new ArrayList<>();
        faultCountByFacility.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    Optional<Facility> facOpt = facilityRepository.findById(entry.getKey());
                    Map<String, Object> item = new HashMap<>();
                    item.put("facilityId", entry.getKey());
                    item.put("faultCount", entry.getValue());
                    item.put("facilityName", facOpt.isPresent() ? facOpt.get().getName() : "未知设施");
                    topFaults.add(item);
                });

        Map<String, Object> result = new HashMap<>();
        result.put("faultRanking", topFaults);
        return Result.success(result);
    }

    /**
     * 获取维修统计数据汇总
     * @return 汇总统计数据
     */
    @GetMapping("/stats/summary")
    public Result<Map<String, Object>> getSummaryStats() {
        List<Maintenance> allMaintenances = maintenanceRepository.findAll();

        int total = allMaintenances.size();
        int pending = (int) allMaintenances.stream().filter(m -> "PENDING".equals(m.getStatus())).count();
        int inProgress = (int) allMaintenances.stream().filter(m -> "IN_PROGRESS".equals(m.getStatus())).count();
        int completed = (int) allMaintenances.stream().filter(m -> "COMPLETED".equals(m.getStatus())).count();

        long totalFacilities = facilityRepository.count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalFacilities", totalFacilities);
        result.put("totalMaintenance", total);
        result.put("pendingMaintenance", pending);
        result.put("inProgressMaintenance", inProgress);
        result.put("completedMaintenance", completed);
        return Result.success(result);
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkPendingMaintenances() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteenMinutesLater = now.plusMinutes(15);

        List<Maintenance> pendingMaintenances = maintenanceRepository.findAll().stream()
                .filter(m -> "PENDING".equals(m.getStatus()))
                .filter(m -> m.getStartTime() != null)
                .filter(m -> m.getStartTime().isBefore(fifteenMinutesLater) || m.getStartTime().isEqual(fifteenMinutesLater))
                .filter(m -> m.getStartTime().isAfter(now.minusMinutes(15)))
                .collect(Collectors.toList());

        for (Maintenance maintenance : pendingMaintenances) {
            maintenance.setStatus("IN_PROGRESS");
            maintenanceRepository.save(maintenance);

            Optional<Facility> facilityOpt = facilityRepository.findById(maintenance.getFacilityId());
            if (facilityOpt.isPresent()) {
                Facility facility = facilityOpt.get();
                if (!"MAINTENANCE".equals(facility.getStatus())) {
                    facility.setStatus("MAINTENANCE");
                    facilityRepository.save(facility);
                }
            }
        }

        if (!pendingMaintenances.isEmpty()) {
            System.out.println("自动更新 " + pendingMaintenances.size() + " 个维护任务状态为进行中");
        }
    }

    private void syncFacilityStatus(Maintenance maintenance, String oldStatus) {
        if (maintenance.getFacilityId() == null) {
            return;
        }

        Optional<Facility> facilityOpt = facilityRepository.findById(maintenance.getFacilityId());
        if (!facilityOpt.isPresent()) {
            return;
        }

        Facility facility = facilityOpt.get();
        String newStatus = maintenance.getStatus();
        if ("IN_PROGRESS".equals(newStatus) && !"IN_PROGRESS".equals(oldStatus)) {
            facility.setStatus("MAINTENANCE");
            facilityRepository.save(facility);
            return;
        }

        if ("COMPLETED".equals(newStatus) && !"COMPLETED".equals(oldStatus)) {
            if ("MAINTENANCE".equals(facility.getStatus())) {
                facility.setStatus("AVAILABLE");
                facilityRepository.save(facility);
            }
            return;
        }

        if (maintenance.getStartTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fifteenMinutesBeforeStart = maintenance.getStartTime().minusMinutes(15);
            if ((now.isAfter(fifteenMinutesBeforeStart) || now.isEqual(fifteenMinutesBeforeStart))
                    && !"IN_PROGRESS".equals(newStatus)
                    && !"COMPLETED".equals(newStatus)) {
                if (!"MAINTENANCE".equals(facility.getStatus())) {
                    facility.setStatus("MAINTENANCE");
                    facilityRepository.save(facility);
                }
            }
        }
    }
}
