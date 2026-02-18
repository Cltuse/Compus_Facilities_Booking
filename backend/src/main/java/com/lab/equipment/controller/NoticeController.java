package com.lab.equipment.controller;

import com.lab.equipment.common.Result;
import com.lab.equipment.entity.Notice;
import com.lab.equipment.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 通知公告控制器
 * 提供通知公告的增删改查等功能
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeRepository noticeRepository;

    /**
     * 获取所有通知公告
     * @return 通知公告列表
     */
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        List<Notice> notices = noticeRepository.findAll();
        return Result.success(notices);
    }

    /**
     * 获取已发布的通知公告
     * @return 已发布的通知公告列表
     */
    @GetMapping("/published")
    public Result<List<Notice>> getPublished() {
        List<Notice> notices = noticeRepository.findByStatus("PUBLISHED");
        return Result.success(notices);
    }

    /**
     * 根据ID获取通知公告详情
     * @param id 通知公告ID
     * @return 通知公告详情
     */
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        Optional<Notice> notice = noticeRepository.findById(id);
        if (notice.isPresent()) {
            return Result.success(notice.get());
        }
        return Result.error("通知不存在");
    }

    /**
     * 创建通知公告
     * @param notice 通知公告信息
     * @return 创建的通知公告信息
     */
    @PostMapping
    public Result<Notice> create(@RequestBody Notice notice) {
        Notice savedNotice = noticeRepository.save(notice);
        return Result.success("创建成功", savedNotice);
    }

    /**
     * 更新通知公告
     * @param id 通知公告ID
     * @param notice 更新的通知公告信息
     * @return 更新后的通知公告信息
     */
    @PutMapping("/{id}")
    public Result<Notice> update(@PathVariable Long id, @RequestBody Notice notice) {
        if (!noticeRepository.existsById(id)) {
            return Result.error("通知不存在");
        }
        notice.setId(id);
        Notice savedNotice = noticeRepository.save(notice);
        return Result.success("更新成功", savedNotice);
    }

    /**
     * 删除通知公告
     * @param id 通知公告ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!noticeRepository.existsById(id)) {
            return Result.error("通知不存在");
        }
        noticeRepository.deleteById(id);
        return Result.success("删除成功", null);
    }
}
