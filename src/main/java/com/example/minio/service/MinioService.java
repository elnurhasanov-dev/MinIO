package com.example.minio.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private static final String URL_CACHE_PREFIX = "minio:url:";

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Async
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

    public List<String> listFiles(String folder) {
        List<String> fileNames = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(folder)
                            .recursive(true) // Brings all objects in folder/, including subfolders
                            .build()
            );

            for (Result<Item> result : results) {
                String objectName = result.get().objectName();
                System.out.println(objectName);
                if (!objectName.endsWith("/")) {
                    fileNames.add(objectName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("FILE_LIST_FAILED");
        }
        return fileNames;
    }

    public String getUrl(String folder, String objectName) {
        String objectPath = folder + "/" + objectName;

        if (!checkFileExists(objectPath)) {
            throw new RuntimeException("FILE_NOT_FOUND");
        }

        // String cacheKey = URL_CACHE_PREFIX + objectPath;
        // String cachedUrl = redisTemplate.opsForValue().get(cacheKey);

        // if (cachedUrl != null) return cachedUrl;

        // String newUrl = generateUrl(folder, objectName);
        // redisTemplate.opsForValue().set(cacheKey, newUrl, 7, TimeUnit.DAYS);

        return generateUrl(folder, objectName);
    }

    public String generateUrl(String folder, String objectName) {
        try {
            String fullPath = folder.isEmpty() ? objectName : folder + "/" + objectName;
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fullPath)
                            .expiry(7, TimeUnit.DAYS)
                            .extraQueryParams(Map.of( // The file will open in the browser (not download).
                                    "response-content-disposition",
                                    "attachment; filename=\"" + objectName + "\""
                            ))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate URL", e);
        }
    }

    public InputStreamResource downloadFile(String folderName, String fileName) {
        try {
            String objectName = folderName + "/" + fileName;
            if (!checkFileExists(objectName)) {
                throw new RuntimeException("FILE_NOT_FOUND");
            }

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new RuntimeException("DOWNLOAD_FAILED");
        }
    }

    public InputStreamResource downloadFolder(String folderName) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(folderName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.objectName().endsWith("/")) {
                    try (InputStream stream = minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(item.objectName())
                                    .build())) {

                        zos.putNextEntry(new ZipEntry(item.objectName().substring(folderName.length() + 1)));
                        stream.transferTo(zos);
                        zos.closeEntry();
                    }
                }
            }
            zos.finish();
            return new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException("FOLDER_DOWNLOAD_FAILED");
        }
    }

    public void createFolder(String folderName) {
        try {
            if (!checkFolderExists(folderName)) {

                String normalizedFolderName = folderName.endsWith("/") ? folderName : folderName + "/";
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(normalizedFolderName)
                                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                                .build()
                );
            } else {
                throw new RuntimeException("FOLDER_ALREADY_EXISTS");
            }
        } catch (Exception e) {
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
            String normalizedFolderName = folderName.endsWith("/") ? folderName : folderName + "/";
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(normalizedFolderName)
                            .maxKeys(1)
                            .build()
            );

            for (Result<Item> result : results) {
                if (result.get().objectName().startsWith(normalizedFolderName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error checking folder existence {}: {}", folderName, e.getMessage());
        }
        return false;
    }

}
