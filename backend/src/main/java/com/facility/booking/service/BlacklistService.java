package com.facility.booking.service;

import com.facility.booking.entity.Blacklist;
import com.facility.booking.entity.OperationLog;
import com.facility.booking.repository.BlacklistRepository;
import com.facility.booking.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 黑名单服务类
 * 处理黑名单相关的业务逻辑和定时任务
 */
@Service
public class BlacklistService {

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    /**
     * 自动处理过期的黑名单记录
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void autoExpireBlacklist() {
        LocalDateTime now = LocalDateTime.now();
        List<Blacklist> expiredList = blacklistRepository.findByStatusAndEndTimeBefore("ACTIVE", now);
        
        int updatedCount = 0;
        for (Blacklist blacklist : expiredList) {
            blacklist.setStatus("EXPIRED");
            blacklistRepository.save(blacklist);
            updatedCount++;
            
            // 记录操作日志
            OperationLog log = new OperationLog();
            log.setOperationType("AUTO_EXPIRE_BLACKLIST");
            log.setTargetId(blacklist.getUserId());
            log.setDetail("黑名单自动过期，原因为：" + blacklist.getReason());
            operationLogRepository.save(log);
        }
        
        if (updatedCount > 0) {
            System.out.println("自动过期处理完成，共处理 " + updatedCount + " 条黑名单记录");
        }
    }

    /**
     * 检查用户是否在黑名单中
     * @param userId 用户ID
     * @return true如果在黑名单中，false否则
     */
    public boolean isUserBlacklisted(Long userId) {
        return blacklistRepository.findByUserIdAndStatus(userId, "ACTIVE").isPresent();
    }

    /**
     * 获取用户的有效黑名单记录
     * @param userId 用户ID
     * @return 黑名单记录，如果不存在则返回空
     */
    public Blacklist getUserActiveBlacklist(Long userId) {
        return blacklistRepository.findByUserIdAndStatus(userId, "ACTIVE").orElse(null);
    }
}