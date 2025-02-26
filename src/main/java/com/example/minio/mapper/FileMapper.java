package com.example.minio.mapper;


import com.example.minio.dto.FileDto;
import com.example.minio.entity.File;


public class FileMapper {

    public static FileDto convert(File file) {
        return new FileDto(
                file.getName(),
                file.getExtension(),
                file.getSize(),
                file.getFileType().name()
        );
    }
}
