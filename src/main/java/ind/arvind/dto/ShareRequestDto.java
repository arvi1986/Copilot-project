package ind.arvind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class ShareRequestDto {
    @NotNull(message = "folderpath must not be null")
    @NotBlank(message = "folderpath must not be blank")
    @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$", message = "folderpath must be a valid URL")
    private String folderpath;

    @NotNull(message = "emails must not be null")
    @NotEmpty(message = "emails must not be empty")
    private List<@NotBlank(message = "email must not be blank") String> emails;
}

