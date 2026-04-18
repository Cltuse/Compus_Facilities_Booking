package com.facility.booking.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileStoragePathUtilsTest {

    @Test
    void shouldResolveRelativeUploadDirectoryAgainstWorkingDirectory() {
        Path expected = Path.of("files").toAbsolutePath().normalize();

        Path actual = FileStoragePathUtils.resolveUploadRoot("files");

        assertEquals(expected, actual);
    }

    @Test
    void shouldKeepAbsoluteUploadDirectoryUnchanged() {
        Path absolutePath = Path.of(System.getProperty("java.io.tmpdir"), "facility-files").toAbsolutePath().normalize();

        Path actual = FileStoragePathUtils.resolveUploadRoot(absolutePath.toString());

        assertEquals(absolutePath, actual);
    }

    @Test
    void shouldResolveStoredFileInsideUploadRoot() {
        Path uploadRoot = Path.of(System.getProperty("java.io.tmpdir"), "facility-files").toAbsolutePath().normalize();
        Path expected = uploadRoot.resolve("facility/demo.png").normalize();

        Path actual = FileStoragePathUtils.resolveStoredFile(uploadRoot.toString(), "/files/facility/demo.png");

        assertEquals(expected, actual);
    }

    @Test
    void shouldRejectPathTraversalWhenDeletingStoredFile() {
        Path uploadRoot = Path.of(System.getProperty("java.io.tmpdir"), "facility-files").toAbsolutePath().normalize();

        assertThrows(IllegalArgumentException.class,
                () -> FileStoragePathUtils.resolveStoredFile(uploadRoot.toString(), "/files/../secret.txt"));
    }
}
