package com.neeraj.fileUpload.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.neeraj.fileUpload.config.FileStorageProperties;
import com.neeraj.fileUpload.entity.FileMetadata;
import com.neeraj.fileUpload.exception.FileStorageException;
import com.neeraj.fileUpload.repository.FileRepository;
import com.neeraj.fileUpload.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    private final Path storageLocation;
    private final FileRepository fileRepository;
    private final FileStorageProperties properties;
    private final Tika tika = new Tika();

    private static final Map<String, String> ALLOWED_EXTENSIONS = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "pdf", "application/pdf"
    );

    public FileServiceImpl(FileStorageProperties properties,
                           FileRepository fileRepository) {

        this.properties = properties;
        this.fileRepository = fileRepository;

        this.storageLocation = Paths.get(properties.getUploadDir())
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public FileMetadata upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        if (file.getSize() > properties.getMaxSize()) {
            throw new FileStorageException("File size exceeds limit");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalName.contains("..")) {
            throw new FileStorageException("Invalid file name");
        }

        try {
            String detectedType = tika.detect(file.getInputStream());
            String extension = getExtension(originalName);
            String expectedType = ALLOWED_EXTENSIONS.get(extension);

            if (expectedType == null || !expectedType.equals(detectedType)) {
                throw new FileStorageException("File content does not match extension");
            }

            String storedName = UUID.randomUUID() + "_" + originalName;

            Path targetLocation = storageLocation.resolve(storedName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            FileMetadata metadata = new FileMetadata();
            metadata.setOriginalName(originalName);
            metadata.setStoredName(storedName);
            metadata.setContentType(detectedType);
            metadata.setSize(file.getSize());
            metadata.setUploadedAt(LocalDateTime.now());

            return fileRepository.save(metadata);

        } catch (IOException ex) {
            throw new FileStorageException("File upload failed", ex);
        }
    }
    private String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index == -1) return "";
        return filename.substring(index + 1).toLowerCase();
    }

    @Override
    public Resource download(Long id) {

        FileMetadata metadata = fileRepository.findById(id)
                .orElseThrow(() -> new FileStorageException("File not found"));

        Path filePath = storageLocation.resolve(metadata.getStoredName());

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File not found on disk");
            }

            return resource;

        } catch (MalformedURLException ex) {
            throw new FileStorageException("Error reading file", ex);
        }
    }
}