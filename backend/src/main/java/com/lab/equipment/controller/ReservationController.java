package com.lab.equipment.controller;

import com.lab.equipment.common.Result;
import com.lab.equipment.entity.Equipment;
import com.lab.equipment.entity.Reservation;
import com.lab.equipment.entity.User;
import com.lab.equipment.repository.EquipmentRepository;
import com.lab.equipment.repository.ReservationRepository;
import com.lab.equipment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 设备预约控制器
 * 提供设备预约的增删改查、状态管理等功能
 */
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有预约记录
     * @return 预约记录列表
     */
    @GetMapping("/list")
    public Result<List<Reservation>> list() {
        List<Reservation> reservations = reservationRepository.findAll();
        enrichReservations(reservations);
        return Result.success(reservations);
    }

    /**
     * 根据用户ID获取预约记录
     * @param userId 用户ID
     * @return 该用户的预约记录列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Reservation>> getByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        enrichReservations(reservations);
        return Result.success(reservations);
    }

    /**
     * 获取待审核的预约记录
     * @return 待审核的预约记录列表
     */
    @GetMapping("/pending")
    public Result<List<Reservation>> getPending() {
        List<Reservation> reservations = reservationRepository.findByStatus("PENDING");
        enrichReservations(reservations);
        return Result.success(reservations);
    }

    /**
     * 根据ID获取预约记录详情
     * @param id 预约记录ID
     * @return 预约记录详情
     */
    @GetMapping("/{id}")
    public Result<Reservation> getById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            enrichReservation(res);
            return Result.success(res);
        }
        return Result.error("预约不存在");
    }

    /**
     * 创建预约记录
     * @param reservation 预约记录信息
     * @return 创建的预约记录信息
     */
    @PostMapping
    public Result<Reservation> create(@RequestBody Reservation reservation) {
        reservation.setStatus("PENDING");
        Reservation savedReservation = reservationRepository.save(reservation);
        enrichReservation(savedReservation);
        return Result.success("预约成功", savedReservation);
    }

    /**
     * 更新预约记录
     * @param id 预约记录ID
     * @param reservation 更新的预约记录信息
     * @return 更新后的预约记录信息
     */
    @PutMapping("/{id}")
    public Result<Reservation> update(@PathVariable Long id, @RequestBody Reservation reservation) {
        if (!reservationRepository.existsById(id)) {
            return Result.error("预约不存在");
        }
        reservation.setId(id);
        Reservation savedReservation = reservationRepository.save(reservation);
        enrichReservation(savedReservation);
        return Result.success("更新成功", savedReservation);
    }

    /**
     * 审核通过预约
     * @param id 预约记录ID
     * @param reservation 包含审核备注的预约记录信息
     * @return 审核通过的预约记录信息
     */
    @PutMapping("/{id}/approve")
    public Result<Reservation> approve(@PathVariable Long id, @RequestBody Reservation reservation) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();
        existingReservation.setStatus("APPROVED");
        existingReservation.setAdminRemark(reservation.getAdminRemark());

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("审核通过", savedReservation);
    }

    /**
     * 拒绝预约
     * @param id 预约记录ID
     * @param reservation 包含拒绝原因的预约记录信息
     * @return 被拒绝的预约记录信息
     */
    @PutMapping("/{id}/reject")
    public Result<Reservation> reject(@PathVariable Long id, @RequestBody Reservation reservation) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();
        existingReservation.setStatus("REJECTED");
        existingReservation.setAdminRemark(reservation.getAdminRemark());

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("已拒绝", savedReservation);
    }

    /**
     * 取消预约
     * @param id 预约记录ID
     * @return 取消后的预约记录信息
     */
    @PutMapping("/{id}/cancel")
    public Result<Reservation> cancel(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();
        existingReservation.setStatus("CANCELLED");

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("已取消", savedReservation);
    }

    /**
     * 完成预约
     * @param id 预约记录ID
     * @return 完成后的预约记录信息
     */
    @PutMapping("/{id}/complete")
    public Result<Reservation> complete(@PathVariable Long id) {
        Optional<Reservation> resOpt = reservationRepository.findById(id);
        if (!resOpt.isPresent()) {
            return Result.error("预约不存在");
        }

        Reservation existingReservation = resOpt.get();

        // 只有已通过的预约才能完成
        if (!"APPROVED".equals(existingReservation.getStatus())) {
            return Result.error("只有已通过的预约才能完成");
        }

        // 更新预约状态为已完成
        existingReservation.setStatus("COMPLETED");

        // 更新设备状态为可用
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(existingReservation.getEquipmentId());
        if (equipmentOpt.isPresent()) {
            Equipment equipment = equipmentOpt.get();
            equipment.setStatus("AVAILABLE");
            equipmentRepository.save(equipment);
        }

        Reservation savedReservation = reservationRepository.save(existingReservation);
        enrichReservation(savedReservation);
        return Result.success("预约已完成", savedReservation);
    }

    /**
     * 删除预约记录
     * @param id 预约记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!reservationRepository.existsById(id)) {
            return Result.error("预约不存在");
        }
        reservationRepository.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量丰富预约记录信息，添加设备名称和用户姓名
     * @param reservations 预约记录列表
     */
    private void enrichReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            enrichReservation(reservation);
        }
    }

    /**
     * 丰富单个预约记录信息，添加设备名称和用户姓名
     * @param reservation 预约记录
     */
    private void enrichReservation(Reservation reservation) {
        Optional<Equipment> equipment = equipmentRepository.findById(reservation.getEquipmentId());
        equipment.ifPresent(e -> reservation.setEquipmentName(e.getName()));

        Optional<User> user = userRepository.findById(reservation.getUserId());
        user.ifPresent(u -> reservation.setUserName(u.getRealName()));
    }
}
