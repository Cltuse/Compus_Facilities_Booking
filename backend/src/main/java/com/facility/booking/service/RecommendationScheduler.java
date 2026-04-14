package com.facility.booking.service;

import com.facility.booking.repository.NoticeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecommendationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationScheduler.class);
    
    @Autowired
    private UserCFService userCFService;
    
    @Autowired
    private HotScoreService hotScoreService;
    
    @Autowired
    private RecommendService recommendService;
    
    @Autowired
    private NoticeRepository noticeRepository;
    
    /**
     * 每天凌晨2点更新用户相似度矩阵
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateUserSimilarities() {
        try {
            logger.info("开始更新用户相似度矩阵...");
            userCFService.computeUserSimilarities();
            logger.info("用户相似度矩阵更新完成");
        } catch (Exception e) {
            logger.error("更新用户相似度矩阵失败", e);
        }
    }
    
    /**
     * 每天凌晨3点更新设施热度评分
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void updateHotScores() {
        try {
            logger.info("开始更新设施热度评分...");
            hotScoreService.calculateAllFacilityHotScores();
            logger.info("设施热度评分更新完成");
        } catch (Exception e) {
            logger.error("更新设施热度评分失败", e);
        }
    }
    
    /**
     * 每小时刷新活跃用户的推荐缓存
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshRecommendationCache() {
        try {
            logger.info("开始刷新推荐缓存...");
            recommendService.refreshActiveUserRecommendations();
            logger.info("推荐缓存刷新完成");
        } catch (Exception e) {
            logger.error("刷新推荐缓存失败", e);
        }
    }

    
    /**
     * 每天凌晨4点为所有用户生成推荐
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void generateRecommendations() {
        try {
            logger.info("开始为所有用户生成推荐...");
            recommendService.generateRecommendationsForAllUsers();
            logger.info("用户推荐生成完成");
        } catch (Exception e) {
            logger.error("生成用户推荐失败", e);
        }
    }
    
    /**
     * 系统启动后立即执行一次推荐数据初始化
     */
    @Scheduled(fixedDelay = Long.MAX_VALUE) // 只执行一次
    public void initializeRecommendationData() {
        try {
            logger.info("系统启动，开始初始化推荐数据...");
            
            // 更新热度评分
            hotScoreService.calculateAllFacilityHotScores();
            logger.info("设施热度评分初始化完成");
            
            // 更新用户相似度
            userCFService.computeUserSimilarities();
            logger.info("用户相似度矩阵初始化完成");
            
            // 生成推荐
            recommendService.generateRecommendationsForAllUsers();
            logger.info("用户推荐初始化完成");
            
            logger.info("推荐数据初始化完成");
        } catch (Exception e) {
            logger.error("推荐数据初始化失败", e);
        }
    }
    
    /**
     * 每隔5分钟检查并发布已到时的定时通知
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void publishScheduledNotices() {
        try {
            int count = noticeRepository.publishScheduledNotices(java.time.LocalDateTime.now());
            if (count > 0) {
                logger.info("定时发布{}条通知成功", count);
            }
        } catch (Exception e) {
            logger.error("定时发布通知失败", e);
        }
    }
}