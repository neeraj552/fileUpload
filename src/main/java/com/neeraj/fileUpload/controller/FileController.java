package com.neeraj.fileUpload.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.neeraj.fileUpload.entity.FileMetadata;
import com.neeraj.fileUpload.service.FileService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @Operation(summary = "Upload file")
    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> upload(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(fileService.upload(file));
    }
    @Operation(summary = "Download file by ID")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        Resource resource = fileService.download(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
