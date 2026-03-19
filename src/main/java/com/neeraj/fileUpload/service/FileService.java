package com.neeraj.fileUpload.service;

import org.springframework.web.multipart.MultipartFile;

import com.neeraj.fileUpload.entity.FileMetadata;

public interface FileService {
    FileMetadata upload(MultipartFile file);

}
