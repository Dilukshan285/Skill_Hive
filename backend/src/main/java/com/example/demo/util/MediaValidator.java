package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MediaValidator {
    private static final Logger logger = LoggerFactory.getLogger(MediaValidator.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png"};
    private static final String[] ALLOWED_VIDEO_TYPES = {"video/mp4"};

    public void validateMedia(MultipartFile[] images, MultipartFile video) {
        if (images != null) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    validateFile(image, ALLOWED_IMAGE_TYPES, "Image");
                }
            }
        }
        if (video != null && !video.isEmpty()) {
            validateFile(video, ALLOWED_VIDEO_TYPES, "Video");
        }
    }

    private void validateFile(MultipartFile file, String[] allowedTypes, String fileType) {
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.error("{} file size exceeds limit: {}", fileType, file.getSize());
            throw new IllegalArgumentException(fileType + " file size exceeds 10MB");
        }
        String contentType = file.getContentType();
        boolean isValidType = false;
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(contentType)) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            logger.error("Invalid {} file type: {}", fileType, contentType);
            throw new IllegalArgumentException("Invalid " + fileType.toLowerCase() + " file type: " + contentType);
        }
        logger.info("Validated {} file: {}", fileType, file.getOriginalFilename());
    }
}