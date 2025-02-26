package com.example.minio.service;

import com.example.minio.dto.FileDto;
import com.example.minio.entity.File;
import com.example.minio.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.example.minio.mapper.FileMapper.convert;
import static com.example.minio.model.entity.FileType.FILE;
import static org.springframework.web.util.UriUtils.extractFileExtension;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioService minioService;
    private final FileRepository fileRepository;

    @Transactional
    public FileDto uploadFile(MultipartFile file, String folderName) {
        var fileName = file.getOriginalFilename();
        minioService.uploadFile(file, folderName, fileName);
        return convert(fileRepository.save(createFileEntity(fileName, file)));
    }

    private File createFileEntity(String fileName, MultipartFile file) {
        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setExtension(extractFileExtension(fileName));
        fileEntity.setSize(file.getSize());
        fileEntity.setFileType(FILE);
        return fileEntity;
    }
}
