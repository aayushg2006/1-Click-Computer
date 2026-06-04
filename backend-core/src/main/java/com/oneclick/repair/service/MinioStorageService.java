package com.oneclick.repair.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${storage.minio.bucket:oneclick}")
    private String bucket;

    @Value("${storage.minio.enabled:true}")
    private boolean enabled;

    public String store(MultipartFile file, String objectKey) {
        if (!enabled) {
            throw new RuntimeException("MinIO storage is disabled in this environment");
        }
        try (InputStream in = file.getInputStream()) {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(in, Long.valueOf(file.getSize()), Long.valueOf(10L * 1024L * 1024L))
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .build());
            return objectKey;
        } catch (IOException e) {
            throw new RuntimeException("Unable to read uploaded file", e);
        } catch (Exception e) {
            throw new RuntimeException("Unable to store file in MinIO", e);
        }
    }

    public String presignedGetUrl(String objectKey, int expiryMinutes) {
        if (!enabled) {
            throw new RuntimeException("MinIO storage is disabled in this environment");
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(expiryMinutes, TimeUnit.MINUTES)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to create presigned URL", e);
        }
    }

    public String bucketName() {
        return bucket;
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize MinIO bucket", e);
        }
    }
}
