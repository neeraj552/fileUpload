package com.neeraj.fileUpload.repository;

import com.neeraj.fileUpload.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {
}
