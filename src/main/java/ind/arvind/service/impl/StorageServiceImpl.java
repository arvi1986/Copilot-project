package ind.arvind.service.impl;

import ind.arvind.dto.FileUploadRequestDto;
import ind.arvind.dto.FileMetadataUpdateDto;
import ind.arvind.dto.StoredFileResponseDto;
import ind.arvind.entity.FileMetadata;
import ind.arvind.entity.StoredFile;
import ind.arvind.entity.StorageBucket;
import ind.arvind.repository.FileMetadataRepository;
import ind.arvind.repository.StoredFileRepository;
import ind.arvind.repository.StorageBucketRepository;
import ind.arvind.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);
    private static final String ERR_BUCKET_NOT_FOUND = "Bucket not found";
    private static final String ERR_FILE_NOT_FOUND = "File not found";

    private final StoredFileRepository storedFileRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final StorageBucketRepository storageBucketRepository;

    @Override
    @Transactional
    public StoredFileResponseDto uploadFile(FileUploadRequestDto requestDto, String owner) {
        log.info("Uploading file: {} for owner: {} in bucket: {}", requestDto.getFile().getOriginalFilename(), owner, requestDto.getBucketName());
        StorageBucket bucket = storageBucketRepository.findByName(requestDto.getBucketName())
                .orElseThrow(() -> {
                    log.error("{}: {}", ERR_BUCKET_NOT_FOUND, requestDto.getBucketName());
                    return new IllegalArgumentException(ERR_BUCKET_NOT_FOUND);
                });
        MultipartFile file = requestDto.getFile();
        StoredFile storedFile = StoredFile.builder()
                .filename(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(Optional.ofNullable(file.getContentType()).orElse("application/octet-stream"))
                .storagePath("/storage/" + UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .owner(owner)
                .storageBucket(bucket)
                .build();
        StoredFile persistedFile = storedFileRepository.save(storedFile);
        if (Optional.ofNullable(requestDto.getMetadata()).isPresent()) {
            List<FileMetadata> metadataList = requestDto.getMetadata().entrySet().stream()
                    .map(e -> FileMetadata.builder().storedFile(persistedFile).metaKey(e.getKey()).value(e.getValue()).build())
                    .collect(Collectors.toList());
            fileMetadataRepository.saveAll(metadataList);
            storedFile.setMetadata(metadataList);
        }
        log.info("File uploaded successfully: {} (ID: {})", storedFile.getFilename(), storedFile.getId());
        return toDto(storedFile);
    }

    @Override
    public List<StoredFileResponseDto> listFiles(String owner, int page, int size) {
        log.info("Listing files for owner: {}", owner);
        List<StoredFile> files = storedFileRepository.findByOwner(owner); // Add paging if needed
        return files.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public StoredFileResponseDto getFile(Long fileId, String owner) {
        log.info("Retrieving file: {} for owner: {}", fileId, owner);
        StoredFile file = storedFileRepository.findById(fileId)
                .filter(f -> f.getOwner().equals(owner))
                .orElseThrow(() -> {
                    log.error("{}: {}", ERR_FILE_NOT_FOUND, fileId);
                    return new NoSuchElementException(ERR_FILE_NOT_FOUND);
                });
        return toDto(file);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId, String owner) {
        log.info("Deleting file: {} for owner: {}", fileId, owner);
        StoredFile file = storedFileRepository.findById(fileId)
                .filter(f -> f.getOwner().equals(owner))
                .orElseThrow(() -> {
                    log.error("{}: {}", ERR_FILE_NOT_FOUND, fileId);
                    return new NoSuchElementException(ERR_FILE_NOT_FOUND);
                });
        storedFileRepository.delete(file);
        log.info("File deleted: {}", fileId);
    }

    @Override
    @Transactional
    public StoredFileResponseDto updateMetadata(Long fileId, FileMetadataUpdateDto metadataDto, String owner) {
        log.info("Updating metadata for file: {} by owner: {}", fileId, owner);
        StoredFile file = storedFileRepository.findById(fileId)
                .filter(f -> f.getOwner().equals(owner))
                .orElseThrow(() -> {
                    log.error("{}: {}", ERR_FILE_NOT_FOUND, fileId);
                    return new NoSuchElementException(ERR_FILE_NOT_FOUND);
                });
        fileMetadataRepository.deleteAll(file.getMetadata());
        List<FileMetadata> metadataList = metadataDto.getMetadata().entrySet().stream()
                .map(e -> FileMetadata.builder().storedFile(file).metaKey(e.getKey()).value(e.getValue()).build())
                .collect(Collectors.toList());
        fileMetadataRepository.saveAll(metadataList);
        if (file.getMetadata() == null) {
            file.setMetadata(new ArrayList<>());
        } else {
            file.getMetadata().clear();
        }
        file.getMetadata().addAll(metadataList);
        file.setUpdatedAt(Instant.now());
        storedFileRepository.save(file);
        log.info("Metadata updated for file: {}", fileId);
        return toDto(file);
    }

    @Override
    public byte[] getFileContent(Long fileId, String owner) {
        log.info("Retrieving file content for fileId: {} and owner: {}", fileId, owner);
        StoredFile file = storedFileRepository.findById(fileId)
                .filter(f -> f.getOwner().equals(owner))
                .orElseThrow(() -> {
                    log.error("File not found or access denied: {}", fileId);
                    return new NoSuchElementException("File not found or access denied");
                });
        Path filePath = Paths.get(file.getStoragePath());
        if (!Files.exists(filePath)) {
            log.error("File not found on disk: {}", filePath);
            throw new NoSuchElementException("File not found on disk");
        }
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Error reading file content: {}", filePath, e);
            throw new RuntimeException("Error reading file content", e);
        }
    }

    private StoredFileResponseDto toDto(StoredFile file) {
        StoredFileResponseDto dto = new StoredFileResponseDto();
        dto.setId(file.getId());
        dto.setFilename(file.getFilename());
        dto.setSize(file.getSize());
        dto.setContentType(file.getContentType());
        dto.setDownloadUrl("/api/v1/storage/files/" + file.getId() + "/download");
        dto.setCreatedAt(file.getCreatedAt());
        dto.setUpdatedAt(file.getUpdatedAt());
        dto.setOwner(file.getOwner());
        dto.setBucketName(file.getStorageBucket().getName());
        if (Optional.ofNullable(file.getMetadata()).isPresent() && !file.getMetadata().isEmpty()) {
            dto.setMetadata(file.getMetadata().stream().collect(Collectors.toMap(FileMetadata::getMetaKey, FileMetadata::getValue)));
        } else {
            dto.setMetadata(Collections.emptyMap());
        }
        return dto;
    }
}
