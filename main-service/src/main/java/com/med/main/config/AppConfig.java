package com.med.main.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${application.minio.url}")
    private String minioUrl;

    @Value("${application.minio.access-key}")
    private String accessKey;

    @Value("${application.minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        // Добавляем простую проверку, чтобы не падало при старте если конфиг пустой (опционально)
        if (minioUrl == null) minioUrl = "http://localhost:9000";

        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}