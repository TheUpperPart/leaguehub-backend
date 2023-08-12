package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.member.ProfileDto;

public class ProfileFixture {
    public static ProfileDto createProfile() {
        return ProfileDto.builder()
                .profileImageUrl("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1Y")
                .nickName("성우")
                .build();
    }
}