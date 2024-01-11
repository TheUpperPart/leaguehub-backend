package leaguehub.leaguehubbackend.domain.member.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NicknameRequestDto {

    @NotBlank
    @Size(max = 20, message = "닉네임은 20자 이하여야 합니다")
    private String nickName;

}
