package ind.arvind.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class FileMetadataUpdateDto {
    @NotNull
    private Map<String, String> metadata;
}
