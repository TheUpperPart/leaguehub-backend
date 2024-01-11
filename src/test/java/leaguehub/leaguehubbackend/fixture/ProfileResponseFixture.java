package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.domain.member.dto.member.ProfileResponseDto;

public class ProfileResponseFixture {
    public static ProfileResponseDto createProfileResponse() {
        ProfileResponseDto responseDto = ProfileResponseDto.builder()
                .profileId("12345")
                .profileImageUrl("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1Y")
                .nickName("성우")
                .build();

        return responseDto;
    }
}
