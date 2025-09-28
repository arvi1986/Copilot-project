package ind.arvind.controller;

import ind.arvind.dto.FileUploadRequestDto;
import ind.arvind.dto.FileMetadataUpdateDto;
import ind.arvind.dto.StoredFileResponseDto;
import ind.arvind.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    private static final Logger log = LoggerFactory.getLogger(StorageController.class);
    private static final String LOG_JWT_STUB = "JWT extraction is stubbed. Replace with real implementation.";
    private final StorageService storageService;

    @PostMapping(value = "/upload",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<StoredFileResponseDto> uploadFile(
            @RequestPart("file") @Valid MultipartFile file,
            @RequestPart("bucketName") String bucketName,
            @RequestPart(value = "metadata", required = false) Map<String, String> metadata,
            @RequestHeader("Authorization") String authHeader) {
        log.info("Received upload request for file: {}", file.getOriginalFilename());
        String owner = extractOwnerFromJwt(authHeader);

        FileUploadRequestDto requestDto = new FileUploadRequestDto();
        requestDto.setFile(file);
        requestDto.setBucketName(bucketName);
        requestDto.setMetadata(metadata);

        StoredFileResponseDto response = storageService.uploadFile(requestDto, owner);
        log.info("File uploaded: {} (ID: {})", response.getFilename(), response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files")
    public ResponseEntity<List<StoredFileResponseDto>> listFiles(@RequestHeader("Authorization") String authHeader,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Received list files request for owner extracted from JWT.");
        String owner = extractOwnerFromJwt(authHeader);
        List<StoredFileResponseDto> files = storageService.listFiles(owner, page, size);
        log.info("Returning {} files for owner {}", files.size(), owner);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<StoredFileResponseDto> getFile(@PathVariable Long fileId,
                                                         @RequestHeader("Authorization") String authHeader) {
        log.info("Received get file request for fileId: {}", fileId);
        String owner = extractOwnerFromJwt(authHeader);
        StoredFileResponseDto response = storageService.getFile(fileId, owner);
        log.info("Returning file: {} (ID: {})", response.getFilename(), response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId,
                                           @RequestHeader("Authorization") String authHeader) {
        log.info("Received delete file request for fileId: {}", fileId);
        String owner = extractOwnerFromJwt(authHeader);
        storageService.deleteFile(fileId, owner);
        log.info("File deleted: {}", fileId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/files/{fileId}/metadata")
    public ResponseEntity<StoredFileResponseDto> updateMetadata(@PathVariable Long fileId,
                                                                @RequestHeader("Authorization") String authHeader,
                                                                @RequestBody @Valid FileMetadataUpdateDto metadataDto) {
        log.info("Received update metadata request for fileId: {}", fileId);
        String owner = extractOwnerFromJwt(authHeader);
        StoredFileResponseDto response = storageService.updateMetadata(fileId, metadataDto, owner);
        log.info("Metadata updated for file: {} (ID: {})", response.getFilename(), response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId,
                                          @RequestHeader("Authorization") String authHeader) {
        String owner = extractOwnerFromJwt(authHeader);
        StoredFileResponseDto fileDto = storageService.getFile(fileId, owner);
        byte[] fileContent = storageService.getFileContent(fileId, owner);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileDto.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(fileDto.getContentType()))
                .body(fileContent);
    }

    // Stub for extracting owner from JWT
    private String extractOwnerFromJwt(String authHeader) {
        log.warn(LOG_JWT_STUB);
        // TODO: Implement JWT parsing and validation
        return "system";
    }
}
