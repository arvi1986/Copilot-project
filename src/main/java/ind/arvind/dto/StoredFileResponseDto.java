package ind.arvind.dto;

import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
public class StoredFileResponseDto {
    private Long id;
    private String filename;
    private Long size;
    private String contentType;
    private String downloadUrl;
    private Map<String, String> metadata;
    private Instant createdAt;
    private Instant updatedAt;
    private String owner;
    private String bucketName;
}
