package ind.arvind.controller;

import ind.arvind.dto.FileUploadRequestDto;
import ind.arvind.dto.FileMetadataUpdateDto;
import ind.arvind.dto.StoredFileResponseDto;
import ind.arvind.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StorageControllerTest {
    @Mock
    private StorageService storageService;
    @InjectMocks
    private StorageController storageController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();
    }

    @Test
    void uploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello".getBytes());
        FileUploadRequestDto dto = new FileUploadRequestDto();
        dto.setFile(file);
        dto.setBucketName("bucket");
        Mockito.when(storageService.uploadFile(any(FileUploadRequestDto.class), any(String.class)))
                .thenReturn(new StoredFileResponseDto());
        mockMvc.perform(multipart("/api/v1/storage/upload")
                        .file(file)
                        .param("bucketName", "bucket")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void listFiles_success() throws Exception {
        Mockito.when(storageService.listFiles(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/storage/files")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void getFile_success() throws Exception {
        Mockito.when(storageService.getFile(any(Long.class), any(String.class)))
                .thenReturn(new StoredFileResponseDto());
        mockMvc.perform(get("/api/v1/storage/files/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFile_success() throws Exception {
        mockMvc.perform(delete("/api/v1/storage/files/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMetadata_success() throws Exception {
        FileMetadataUpdateDto metadataDto = new FileMetadataUpdateDto();
        Mockito.when(storageService.updateMetadata(any(Long.class), any(FileMetadataUpdateDto.class), any(String.class)))
                .thenReturn(new StoredFileResponseDto());
        mockMvc.perform(put("/api/v1/storage/files/1/metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"metadata\":{\"key\":\"value\"}}")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}