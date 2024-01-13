package leaguehub.leaguehubbackend.domain.member.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginMemberResponse {

    private String accessToken;
    private String refreshToken;
    private boolean verifiedUser;
}

