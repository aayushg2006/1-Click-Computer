package com.oneclick.repair.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${storage.minio.endpoint:http://localhost:9000}") String endpoint,
            @Value("${storage.minio.access-key:minioadmin}") String accessKey,
            @Value("${storage.minio.secret-key:minioadmin123}") String secretKey) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
