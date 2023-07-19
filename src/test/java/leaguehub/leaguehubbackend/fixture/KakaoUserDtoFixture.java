package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;

public class KakaoUserDtoFixture {
    public static KakaoUserDto createKakaoUserDto() {
        KakaoUserDto responseDto = new KakaoUserDto();

        responseDto.setId(2808743059L);
        responseDto.setConnectedAt("2023-05-27T15:53:05Z");

        KakaoUserDto.Properties properties = new KakaoUserDto.Properties();
        properties.setNickname("성우");
        properties.setProfileImage("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1Y");
        properties.setThumbnailImage("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz");
        responseDto.setProperties(properties);

        KakaoUserDto.KakaoAccount kakaoAccount = new KakaoUserDto.KakaoAccount();
        kakaoAccount.setProfileNicknameNeedsAgreement(false);
        kakaoAccount.setProfileImageNeedsAgreement(false);

        KakaoUserDto.KakaoAccount.Profile profile = new KakaoUserDto.KakaoAccount.Profile();
        profile.setNickname("성우");
        profile.setThumbnailImageUrl("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz");

        kakaoAccount.setProfile(profile);
        responseDto.setKakaoAccount(kakaoAccount);

        return responseDto;
    }
}