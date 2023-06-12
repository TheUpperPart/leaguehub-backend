package leaguehub.leaguehubbackend.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginMemberResponse {
    private String nickName;
    private String profileUrl;
    private String accessToken;
    private Long accessTokenExpirationTime;
    private String refreshToken;
    private Long refreshTokenExpirationTime;
}

