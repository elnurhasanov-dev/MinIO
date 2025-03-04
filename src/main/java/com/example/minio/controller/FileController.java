package com.example.minio.controller;

import com.example.minio.dto.FileDto;
import com.example.minio.service.FileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @GetMapping("/list/{prefix}")
    public ResponseEntity<List<String>> listFiles(@PathVariable @Valid @NotBlank String prefix) {
        return ResponseEntity.ok(fileService.listFiles(prefix));
    }

    @GetMapping("/url/{prefix}/{fileName}")
    public ResponseEntity<String> getFileUrl(@PathVariable @Valid @NotBlank String prefix,
                                             @PathVariable @Valid @NotBlank String fileName) {
        return ResponseEntity.ok(fileService.getFileUrl(prefix, fileName));
    }

    @GetMapping("/download/{folder}/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable @Valid @NotBlank String folder,
                                                            @PathVariable @Valid @NotBlank String fileName) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileService.downloadFile(folder, fileName));
    }

    @GetMapping("/download-folder/{folder}")
    public ResponseEntity<InputStreamResource> downloadFolder(@PathVariable @Valid @NotBlank String folder) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + folder + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileService.downloadFolder(folder));
    }

    // NOTE: MinIO does not have a folder concept. When trying to create a folder, the structure will be as follows:
    // profile_pictures/
    // If a separate file is added, it will be saved like this:
    // profile_pictures/FB_IMG_1563927871992.jpg, but profile_pictures/ will still remain empty.
    // Therefore, it is better to directly add a prefix when uploading a file.
    @PostMapping("/create-folder")
    public ResponseEntity<FileDto> createFolder(@RequestParam @Valid @NotBlank String folderName) {
        return ResponseEntity.status(CREATED).body(fileService.createFolder(folderName));
    }

}
