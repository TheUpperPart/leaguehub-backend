package leaguehub.leaguehubbackend.dto.kakao;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoTokenRequestDto {

    private String grantType;
    private String clientId;
    private String redirectUri;
    private String code;

    public KakaoTokenRequestDto(String grantType, String clientId, String redirectUri, String code) {
        this.grantType = grantType;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.code = code;
    }

    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        return params;
    }
}
