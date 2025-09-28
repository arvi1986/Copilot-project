package ind.arvind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Data
public class FileUploadRequestDto {
    @NotNull
    private MultipartFile file;

    @NotBlank
    private String bucketName;

    private Map<String, String> metadata;
}

