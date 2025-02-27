package com.example.minio.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Slf4j
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

    public void createFolder(String folderName) {
        try {
            System.out.println("Working 0: ");

            if (!checkFolderExists(folderName)) {
                System.out.println("Working 7: ");

                String normalizedFolderName = folderName.endsWith("/") ? folderName : folderName + "/";
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(normalizedFolderName)
                                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                                .build()
                );
            } else {
                System.out.println("Working 8: ");
                throw new RuntimeException("FOLDER_ALREADY_EXISTS");
            }
        } catch (Exception e) {
            System.out.println("Working 9: " + e.getMessage());
            throw new RuntimeException("FOLDER_CREATION_FAILED: " + e.getMessage());
        }
    }

    // Helper methods
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

    private boolean checkFolderExists(String folderName) {
        try {
            System.out.println("Working 1");
            String normalizedFolderName = folderName.endsWith("/") ? folderName : folderName + "/";
            System.out.println("Working 2: " + normalizedFolderName);
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(normalizedFolderName)
                            .maxKeys(1)
                            .build()
            );
            System.out.println("Working 3: " + results);

            for (Result<Item> result : results) {
                System.out.println("Working 4: ");
                if (result.get().objectName().startsWith(normalizedFolderName)) {
                    System.out.println("Working 5: ");
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error checking folder existence {}: {}", folderName, e.getMessage());
        }
        System.out.println("Working 6: ");
        return false;
    }

}
