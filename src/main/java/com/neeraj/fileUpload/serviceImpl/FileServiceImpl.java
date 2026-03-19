package com.neeraj.fileUpload.serviceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.tika.Tika;
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

    private static final List<String> ALLOWED_TYPES =
            List.of("image/png", "image/jpeg", "application/pdf");

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

        //  1. Empty check
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        //  2. Size validation
        if (file.getSize() > properties.getMaxSize()) {
            throw new FileStorageException("File size exceeds limit");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());

        //  3. Path traversal protection
        if (originalName.contains("..")) {
            throw new FileStorageException("Invalid file name");
        }

        try {
            //  4. Content validation using Tika (REAL SECURITY)
            String detectedType = tika.detect(file.getInputStream());

            if (!ALLOWED_TYPES.contains(detectedType)) {
                throw new FileStorageException("Invalid file type");
            }

            //  5. UUID naming
            String storedName = UUID.randomUUID() + "_" + originalName;

            Path targetLocation = storageLocation.resolve(storedName);

            //  6. STREAMING (not byte[])
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            //  7. Save metadata
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
}