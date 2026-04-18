package com.facility.booking.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileStoragePathUtils {

    private static final String DEFAULT_UPLOAD_DIR = "files";

    private FileStoragePathUtils() {
    }

    public static Path resolveUploadRoot(String uploadDir) {
        String configuredUploadDir = uploadDir == null || uploadDir.trim().isEmpty()
                ? DEFAULT_UPLOAD_DIR
                : uploadDir.trim();

        Path path = Paths.get(configuredUploadDir);
        if (!path.isAbsolute()) {
            path = path.toAbsolutePath();
        }
        return path.normalize();
    }

    public static Path resolveUploadPath(String uploadDir, String subDir) {
        Path uploadRoot = resolveUploadRoot(uploadDir);
        if (subDir == null || subDir.trim().isEmpty()) {
            return uploadRoot;
        }

        Path resolvedPath = uploadRoot.resolve(Paths.get(subDir.trim())).normalize();
        if (!resolvedPath.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid upload sub directory: " + subDir);
        }
        return resolvedPath;
    }

    public static Path resolveStoredFile(String uploadDir, String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return null;
        }

        Path uploadRoot = resolveUploadRoot(uploadDir);
        String relativePath = extractRelativeFilePath(fileUrl);
        Path resolvedPath = uploadRoot.resolve(relativePath).normalize();
        if (!resolvedPath.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid file path: " + fileUrl);
        }
        return resolvedPath;
    }

    static String extractRelativeFilePath(String fileUrl) {
        String normalizedFileUrl = fileUrl.trim().replace('\\', '/');
        int filesIndex = normalizedFileUrl.indexOf("/files/");
        if (filesIndex >= 0) {
            normalizedFileUrl = normalizedFileUrl.substring(filesIndex + 7);
        }

        while (normalizedFileUrl.startsWith("/")) {
            normalizedFileUrl = normalizedFileUrl.substring(1);
        }

        return normalizedFileUrl;
    }
}
