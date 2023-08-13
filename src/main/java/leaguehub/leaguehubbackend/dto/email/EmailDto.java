package leaguehub.leaguehubbackend.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {

    @NotBlank
    @Schema(description = "이메일 주소", example = "test@naver.com")
    private String email;

}
