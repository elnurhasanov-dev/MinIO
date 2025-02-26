package com.example.minio.dto;

public record FileDto(
        String fileName,
        String extension,
        Long size,
        String fileType
) {
}