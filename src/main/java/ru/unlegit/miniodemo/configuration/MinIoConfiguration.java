package ru.unlegit.miniodemo.configuration;

import io.minio.MinioClient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class MinIoConfiguration {

    private final MinIoProperties minIoProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minIoProperties.url)
                .credentials(minIoProperties.accessKey, minIoProperties.secretKey)
                .build();
    }

    @Setter
    @ConfigurationProperties("minio")
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MinIoProperties {

        String url;
        String accessKey;
        String secretKey;
        @Getter
        String bucket;
    }
}