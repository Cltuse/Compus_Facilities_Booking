package com.facility.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-dir:files}")
    private String uploadDir;

    @Value("${file.base-url:/files}")
    private String baseFileUrl;

    /**
     * 上传文件
     * @param file 上传的文件
     * @return 文件的访问URL
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "");
    }

    /**
     * 上传文件到指定子目录
     * @param file 上传的文件
     * @param subDir 子目录名称
     * @return 文件的访问URL
     */
    public String uploadFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 创建上传目录（包含子目录）
            String targetDir = uploadDir;
            if (subDir != null && !subDir.isEmpty()) {
                targetDir = uploadDir + "/" + subDir;
            }
            
            Path uploadPath = Paths.get(targetDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String uniqueFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
                                   "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;

            // 保存文件
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            // 返回文件访问URL
            if (subDir != null && !subDir.isEmpty()) {
                return baseFileUrl + "/" + subDir + "/" + uniqueFilename;
            } else {
                return baseFileUrl + "/" + uniqueFilename;
            }
            
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文件
     * @param fileUrl 文件的URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String filePathStr;
            
            // 如果 URL 包含协议（如 http://），提取 /files/ 之后的部分
            if (fileUrl.contains("/files/")) {
                int index = fileUrl.indexOf("/files/");
                filePathStr = fileUrl.substring(index + 7); // 移除 "/files/" 部分
            } else {
                // 如果只是相对路径，直接使用
                filePathStr = fileUrl;
            }
            
            // 移除可能的 URL 前导斜杠
            if (filePathStr.startsWith("/")) {
                filePathStr = filePathStr.substring(1);
            }
            
            Path filePath = Paths.get(uploadDir, filePathStr);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证文件类型（只允许图片）
     * @param file 上传的文件
     * @return 是否有效
     */
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        // 检查内容类型
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        // 检查文件扩展名
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            return extension.matches("jpg|jpeg|png|gif|bmp|webp");
        }

        return false;
    }
}
