package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.domain.member.dto.kakao.KakaoTokenResponseDto;

public class KakaoTokenResponseDtoFixture {
    public static KakaoTokenResponseDto createKakaoTokenResponseDto() {
        KakaoTokenResponseDto responseDto = new KakaoTokenResponseDto();

        responseDto.setAccessToken("WjqFifAeMXPI2z-OXYFLthU6ag");
        responseDto.setTokenType("bearer");
        responseDto.setRefreshToken("eQOIu2LcFLCFX-MwSg44zmwIM50MzxDhaVV");
        responseDto.setExpiresIn(21599);
        responseDto.setScope("profile_image profile_nickname");
        responseDto.setRefreshTokenExpiresIn(5183999);

        return responseDto;
    }
}