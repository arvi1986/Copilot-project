package ind.arvind.service;

import ind.arvind.dto.FileUploadRequestDto;
import ind.arvind.dto.FileMetadataUpdateDto;
import ind.arvind.dto.StoredFileResponseDto;
import java.util.List;

public interface StorageService {
    StoredFileResponseDto uploadFile(FileUploadRequestDto requestDto, String owner);
    List<StoredFileResponseDto> listFiles(String owner, int page, int size);
    StoredFileResponseDto getFile(Long fileId, String owner);
    void deleteFile(Long fileId, String owner);
    StoredFileResponseDto updateMetadata(Long fileId, FileMetadataUpdateDto metadataDto, String owner);
    byte[] getFileContent(Long fileId, String owner);
}
