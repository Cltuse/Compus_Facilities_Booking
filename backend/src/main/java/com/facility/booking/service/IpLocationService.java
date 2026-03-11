package com.facility.booking.service;

import com.facility.booking.util.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class IpLocationService {

    @Value("${ip.location.api}")
    private String ipLocationApi;

    /**
     * 根据IP地址获取地理位置
     */
    public String getLocationByIp(String ip) {
        try {
            String url = ipLocationApi + ip + "&json=true";
            String response = HttpUtil.get(url);
            
            // 使用Spring Boot自带的ObjectMapper解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            
            String city = jsonNode.get("city").asText();
            return city.isEmpty() ? "北京" : city;
        } catch (Exception e) {
            return "北京"; // 默认返回北京
        }
    }

    /**
     * 从请求中获取客户端IP地址
     */
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}