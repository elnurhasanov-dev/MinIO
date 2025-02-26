package com.example.minio.repository;

import com.example.minio.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
