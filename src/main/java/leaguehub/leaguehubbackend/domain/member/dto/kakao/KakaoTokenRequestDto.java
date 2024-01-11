package leaguehub.leaguehubbackend.domain.member.dto.kakao;

import lombok.AllArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@AllArgsConstructor
public class KakaoTokenRequestDto {

    private String grantType;
    private String clientId;
    private String redirectUri;
    private String code;
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        return params;
    }
}
