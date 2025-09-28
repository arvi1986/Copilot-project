package ind.arvind.service;

import ind.arvind.dto.FileUploadRequestDto;
import ind.arvind.dto.FileMetadataUpdateDto;
import ind.arvind.dto.StoredFileResponseDto;
import ind.arvind.repository.FileMetadataRepository;
import ind.arvind.repository.StorageBucketRepository;
import ind.arvind.repository.StoredFileRepository;
import ind.arvind.service.impl.StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class StorageServiceImplTest {
    @Mock
    private StoredFileRepository storedFileRepository;
    @Mock
    private FileMetadataRepository fileMetadataRepository;
    @Mock
    private StorageBucketRepository storageBucketRepository;
    @InjectMocks
    private StorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFile_bucketNotFound_throwsException() {
        FileUploadRequestDto dto = new FileUploadRequestDto();
        dto.setBucketName("bucket");
        Mockito.when(storageBucketRepository.findByName(any(String.class))).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> storageService.uploadFile(dto, "owner"));
    }

    @Test
    void getFile_fileNotFound_throwsException() {
        Mockito.when(storedFileRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> storageService.getFile(1L, "owner"));
    }

    @Test
    void listFiles_success() {
        Mockito.when(storedFileRepository.findByOwner(any(String.class))).thenReturn(Collections.emptyList());
        List<StoredFileResponseDto> result = storageService.listFiles("owner", 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void uploadFile_withMetadata_success() {
        FileUploadRequestDto dto = new FileUploadRequestDto();
        dto.setBucketName("bucket");
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn("file.txt");
        Mockito.when(file.getSize()).thenReturn(123L);
        Mockito.when(file.getContentType()).thenReturn("text/plain");
        dto.setFile(file);
        Map<String, String> meta = new HashMap<>();
        meta.put("k1", "v1");
        dto.setMetadata(meta);
        StorageBucket bucket = StorageBucket.builder().id(1L).name("bucket").createdAt(Instant.now()).owner("owner").build();
        when(storageBucketRepository.findByName("bucket")).thenReturn(Optional.of(bucket));
        when(storedFileRepository.save(any(StoredFile.class))).thenAnswer(inv -> inv.getArgument(0));
        when(fileMetadataRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        StoredFileResponseDto result = storageService.uploadFile(dto, "owner");
        assertNotNull(result);
        assertEquals("file.txt", result.getFilename());
        assertTrue(result.getMetadata().containsKey("k1"));
    }

    @Test
    void uploadFile_nullFile_throwsException() {
        FileUploadRequestDto dto = new FileUploadRequestDto();
        dto.setBucketName("bucket");
        dto.setFile(null);
        StorageBucket bucket = StorageBucket.builder().id(1L).name("bucket").createdAt(Instant.now()).owner("owner").build();
        when(storageBucketRepository.findByName("bucket")).thenReturn(Optional.of(bucket));
        assertThrows(NullPointerException.class, () -> storageService.uploadFile(dto, "owner"));
    }

    @Test
    void updateMetadata_emptyMetadata_success() {
        StoredFile file = StoredFile.builder().id(1L).owner("owner").storageBucket(StorageBucket.builder().name("bucket").build()).metadata(new ArrayList<>()).build();
        when(storedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileMetadataRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storedFileRepository.save(any(StoredFile.class))).thenAnswer(inv -> inv.getArgument(0));
        FileMetadataUpdateDto metadataDto = new FileMetadataUpdateDto();
        metadataDto.setMetadata(Collections.emptyMap());
        StoredFileResponseDto result = storageService.updateMetadata(1L, metadataDto, "owner");
        assertNotNull(result);
        assertTrue(result.getMetadata().isEmpty());
    }

    @Test
    void deleteFile_repositoryThrows_propagatesException() {
        StoredFile file = StoredFile.builder().id(1L).owner("owner").storageBucket(StorageBucket.builder().name("bucket").build()).build();
        when(storedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        doThrow(new RuntimeException("DB error")).when(storedFileRepository).delete(any(StoredFile.class));
        assertThrows(RuntimeException.class, () -> storageService.deleteFile(1L, "owner"));
    }

    @Test
    void getFile_wrongOwner_throwsException() {
        StoredFile file = StoredFile.builder().id(1L).filename("file.txt").owner("otherOwner").storageBucket(StorageBucket.builder().name("bucket").build()).build();
        when(storedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        assertThrows(NoSuchElementException.class, () -> storageService.getFile(1L, "owner"));
    }
}
