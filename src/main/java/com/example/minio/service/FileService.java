package com.example.minio.service;

import com.example.minio.dto.FileDto;
import com.example.minio.entity.File;
import com.example.minio.model.entity.FileType;
import com.example.minio.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.minio.mapper.FileMapper.convert;
import static com.example.minio.model.entity.FileType.FILE;
import static com.example.minio.model.entity.FileType.FOLDER;
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

    public FileDto createFolder(String folderName) {
        minioService.createFolder(folderName);
        return convert(fileRepository.save(createFolderEntity(folderName)));
    }

    public List<String> listFiles(String folder) {
        return minioService.listFiles(folder);
    }

    private File createFileEntity(String fileName, MultipartFile file) {
        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setExtension(extractFileExtension(fileName));
        fileEntity.setSize(file.getSize());
        fileEntity.setFileType(FILE);
        return fileEntity;
    }

    private File createFolderEntity(String folderName) {
        File folderEntity = new File();
        folderEntity.setName(folderName);
        folderEntity.setExtension("");
        folderEntity.setFileType(FOLDER);
        folderEntity.setSize(0L);
        return folderEntity;
    }
}
