package leaguehub.leaguehubbackend.domain.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {

    @NotBlank
    @Email
    @Schema(description = "이메일 주소", example = "test@naver.com")
    private String email;

}
