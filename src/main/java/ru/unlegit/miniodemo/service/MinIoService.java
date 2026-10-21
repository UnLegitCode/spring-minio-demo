package ru.unlegit.miniodemo.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.unlegit.miniodemo.configuration.MinIoConfiguration;
import ru.unlegit.miniodemo.dto.FileData;
import ru.unlegit.miniodemo.util.FileUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinIoService {

    MinioClient minioClient;
    MinIoConfiguration.MinIoProperties minIoProperties;

    @PostConstruct
    public void setUp() throws MinioException {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(minIoProperties.getBucket()).build()
        );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(minIoProperties.getBucket()).build()
            );
            log.info("MinIO bucket '{}' has been created", minIoProperties.getBucket());
        }
    }

    public String uploadFile(MultipartFile file) throws MinioException, IOException {
        var objectName = "%s.%s".formatted(UUID.randomUUID(), FileUtil.getFileExtension(file));

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minIoProperties.getBucket())
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1L)
                .contentType(file.getContentType())
                .build()
        );

        log.info("File '{} has been uploaded as {}'", file.getOriginalFilename(), objectName);

        return objectName;
    }

    public FileData downloadFile(String objectName) throws MinioException {
        GetObjectResponse object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minIoProperties.getBucket())
                        .object(objectName)
                        .build()
        );

        return new FileData(
                object,
                object.headers().get("Content-Length"),
                object.headers().get("Content-Type")
        );
    }

    public List<String> listFiles() throws MinioException {
        var results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(minIoProperties.getBucket())
                .build()
        );
        var objectNames = new LinkedList<String>();

        for (Result<Item> result : results) {
            objectNames.add(result.get().objectName());
        }

        return objectNames;
    }

    public void deleteFile(String objectName) throws MinioException {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minIoProperties.getBucket())
                .object(objectName)
                .build()
        );

        log.info("File {} has been deleted", objectName);
    }

    public String getPresignedUrl(String objectName) throws MinioException {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Http.Method.GET)
                .bucket(minIoProperties.getBucket())
                .object(objectName)
                .expiry(10, TimeUnit.MINUTES)
                .build()
        );
    }
}