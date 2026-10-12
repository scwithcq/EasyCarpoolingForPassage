package com.chat.component;

import com.chat.constant.MessageConstant;
import com.chat.exception.BaseException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 上传文件校验：限制文件类型与大小，避免任意文件被上传到 OSS
 */
public class FileUploadValidator {

    /** 允许的图片后缀 */
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp");

    /** 允许的最大文件大小：5MB */
    private static final long MAX_SIZE = 5 * 1024 * 1024L;

    private FileUploadValidator() {
    }

    /**
     * 校验通过返回统一格式的后缀（含 .），校验不通过抛出业务异常
     */
    public static String validateAndGetExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(MessageConstant.FILE_EMPTY);
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BaseException(MessageConstant.FILE_TOO_LARGE);
        }
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
            throw new BaseException(MessageConstant.FILE_TYPE_NOT_ALLOWED);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new BaseException(MessageConstant.FILE_TYPE_NOT_ALLOWED);
        }
        return "." + extension.toLowerCase(Locale.ROOT);
    }
}
