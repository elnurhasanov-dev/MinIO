package com.example.minio.controller;

import com.example.minio.dto.FileDto;
import com.example.minio.service.FileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileDto> uploadFile(@RequestParam @Valid @NotNull MultipartFile file,
                                              @RequestParam @Valid @NotBlank String folderName) {
        return ResponseEntity.status(CREATED).body(fileService.uploadFile(file, folderName));
    }

    @PostMapping("/create-folder")
    public ResponseEntity<FileDto> createFolder(@RequestParam @Valid @NotBlank String folderName) {
        return ResponseEntity.status(CREATED).body(fileService.createFolder(folderName));
    }
}
