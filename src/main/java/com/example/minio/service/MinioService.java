package com.example.minio.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {
    private static final String URL_CACHE_PREFIX = "minio:url:";
    private final MinioClient minioClient;
    @Value("${minio.bucket.name}")
    private String bucketName;

    public void uploadFile(MultipartFile file, String folderName, String fileName) {
        try {
            String objectName = folderName + "/" + fileName;

            if (checkFileExists(objectName)) {
                throw new RuntimeException("FILE_ALREADY_EXISTS");
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
        } catch (Exception ex) {
            throw new RuntimeException("UPLOAD_FAILED");
        }

    }

    private boolean checkFileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
