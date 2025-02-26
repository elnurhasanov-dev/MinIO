package com.example.minio.entity;

import com.example.minio.model.entity.FileType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "files")
public class File extends BaseEntity {

    private String name;
    private String extension;
    private Long size;

    @Enumerated(EnumType.STRING)
    private FileType fileType;
}