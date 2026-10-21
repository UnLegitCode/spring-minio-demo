package ru.unlegit.miniodemo.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@UtilityClass
public class FileUtil {

    public String getFileExtension(MultipartFile file) {
        var fileNameParts = Objects.requireNonNull(
                file.getOriginalFilename(), "original file name is absent"
        ).split("\\.");

        return fileNameParts[fileNameParts.length - 1];
    }
}