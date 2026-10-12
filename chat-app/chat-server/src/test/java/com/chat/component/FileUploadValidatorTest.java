package com.chat.component;

import com.chat.exception.BaseException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 上传文件白名单校验单元测试
 */
class FileUploadValidatorTest {

    private MockMultipartFile file(String filename, byte[] content) {
        return new MockMultipartFile("file", filename, "application/octet-stream", content);
    }

    @Test
    void shouldReturnLowerCaseExtensionWhenAllowed() {
        assertEquals(".png", FileUploadValidator.validateAndGetExtension(file("a.PNG", new byte[]{1})));
        assertEquals(".jpg", FileUploadValidator.validateAndGetExtension(file("照片.jpg", new byte[]{1})));
    }

    @Test
    void shouldRejectWhenFileIsEmpty() {
        MockMultipartFile empty = file("a.png", new byte[0]);
        assertThrows(BaseException.class, () -> FileUploadValidator.validateAndGetExtension(empty));
        assertThrows(BaseException.class, () -> FileUploadValidator.validateAndGetExtension(null));
    }

    @Test
    void shouldRejectWhenExtensionNotAllowed() {
        assertThrows(BaseException.class, () -> FileUploadValidator.validateAndGetExtension(file("a.exe", new byte[]{1})));
        assertThrows(BaseException.class, () -> FileUploadValidator.validateAndGetExtension(file("noext", new byte[]{1})));
    }

    @Test
    void shouldRejectWhenFileTooLarge() {
        byte[] big = new byte[5 * 1024 * 1024 + 1];
        assertThrows(BaseException.class, () -> FileUploadValidator.validateAndGetExtension(file("a.png", big)));
    }
}