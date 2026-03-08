package com.facility.booking;

import com.facility.booking.entity.Facility;
import com.facility.booking.entity.RuleConfig;
import com.facility.booking.service.RuleConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 规则配置测试类
 */
@SpringBootTest
public class RuleConfigTest {

    @Autowired
    private RuleConfigService ruleConfigService;

    @Test
    public void testValidateDuration() {
        RuleConfig rule = new RuleConfig();
        rule.setMinDurationMinutes(30);
        rule.setMaxDurationMinutes(120);

        // 测试有效时长
        assertTrue(ruleConfigService.validateDuration(rule, 60).isSuccess());

        // 测试时长太短
        assertFalse(ruleConfigService.validateDuration(rule, 15).isSuccess());

        // 测试时长太长
        assertFalse(ruleConfigService.validateDuration(rule, 180).isSuccess());
    }

    @Test
    public void testValidateOperatingHours() {
        RuleConfig rule = new RuleConfig();
        rule.setOpenTime(LocalTime.of(8, 0));
        rule.setCloseTime(LocalTime.of(22, 0));

        LocalDateTime validStart = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime validEnd = LocalDateTime.of(2024, 1, 1, 11, 0);

        LocalDateTime invalidStart = LocalDateTime.of(2024, 1, 1, 7, 0);
        LocalDateTime invalidEnd = LocalDateTime.of(2024, 1, 1, 8, 30);

        // 测试有效时间
        assertTrue(ruleConfigService.validateOperatingHours(rule, validStart, validEnd).isSuccess());

        // 测试无效时间
        assertFalse(ruleConfigService.validateOperatingHours(rule, invalidStart, invalidEnd).isSuccess());
    }

    @Test
    public void testGetApplicableRuleConfig() {
        Facility facility = new Facility();
        facility.setCategory("会议室");

        // 测试获取规则配置（可能返回null如果没有配置）
        RuleConfig ruleConfig = ruleConfigService.getApplicableRuleConfig(facility);
        // 如果没有配置规则，应该返回null
        assertNotNull(ruleConfig); // 如果有默认规则的话
    }
}