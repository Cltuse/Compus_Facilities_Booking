package com.facility.booking.config;

import com.facility.booking.entity.RuleConfig;
import com.facility.booking.repository.RuleConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * 规则配置初始化器
 * 系统启动时创建默认的全局规则配置
 */
@Component
public class RuleConfigInitializer implements CommandLineRunner {

    @Autowired
    private RuleConfigRepository ruleConfigRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在全局默认规则
        List<RuleConfig> globalRules = ruleConfigRepository.findByCategoryIdIsNullOrderByCreatedAtDesc();
        if (globalRules.isEmpty()) {
            // 创建默认全局规则配置
            RuleConfig defaultRule = new RuleConfig();
            defaultRule.setCategoryId(null); // 全局默认规则
            defaultRule.setMinDurationMinutes(30); // 最短30分钟
            defaultRule.setMaxDurationMinutes(120); // 最长2小时
            defaultRule.setAdvanceDaysMax(7); // 最多提前7天预约
            defaultRule.setAdvanceCutoffMinutes(60); // 至少提前1小时预约
            defaultRule.setAllowSameDayBooking(true); // 允许当日预约
            defaultRule.setMaxBookingsPerDay(2); // 每天最多2次预约
            defaultRule.setMaxActiveBookings(3); // 最多3个活跃预约
            defaultRule.setCancelDeadlineMinutes(30); // 提前30分钟可取消
            defaultRule.setNeedApproval(false); // 不需要审批
            defaultRule.setOpenTime(LocalTime.of(8, 0)); // 开放时间 08:00
            defaultRule.setCloseTime(LocalTime.of(22, 0)); // 关闭时间 22:00
            defaultRule.setTimeSlotMinutes(30); // 时间片30分钟
            
            ruleConfigRepository.save(defaultRule);
            System.out.println("已创建默认全局规则配置");
        }
    }
}