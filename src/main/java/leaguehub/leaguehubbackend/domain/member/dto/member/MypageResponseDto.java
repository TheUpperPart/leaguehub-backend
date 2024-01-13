package leaguehub.leaguehubbackend.domain.member.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MypageResponseDto {

    private String profileImageUrl;

    private String nickName;

    private String email;

    private boolean userEmailVerified;
}