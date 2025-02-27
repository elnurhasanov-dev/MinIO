package com.example.minio.controller;

import com.example.minio.dto.FileDto;
import com.example.minio.service.FileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/list/{folder}")
    public ResponseEntity<List<String>> listFiles(@PathVariable @Valid @NotBlank String folder) {
        return ResponseEntity.ok(fileService.listFiles(folder));
    }
}
