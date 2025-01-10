package com.wearltnow.service;

import com.cloudinary.Cloudinary;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {
    Cloudinary cloudinary;

    public String uploadImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }


        String originalFilename = image.getOriginalFilename();

        String publicId = FilenameUtils.getBaseName(originalFilename); // Lấy phần tên mà không có phần mở rộng
        String format = FilenameUtils.getExtension(originalFilename); // Lấy phần mở rộng của tệp
        log.info(originalFilename+ "|" + format + "|" + publicId);
        try {
            // Upload với public_id để giữ tên
            Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(),
                    ObjectUtils.asMap("public_id", publicId, "format", format));
            return (String) uploadResult.get("url");
        } catch (Exception e) {
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
