package com.facility.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:files}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传目录的静态资源映射
        // 将 /files/** 映射到实际的文件系统路径，支持子目录
        String basePath = System.getProperty("user.dir") + "/" + uploadDir + "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + basePath);
    }
}