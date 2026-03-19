package com.neeraj.fileUpload.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalName;
    private String contentType;
    private String storedName;
    private Long size;
    private LocalDateTime uploadDate;
    private LocalDateTime uploadedAt;
}
