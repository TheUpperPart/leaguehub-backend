package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.domain.member.dto.member.MypageResponseDto;

public class MypageResponseFixture {
    public static MypageResponseDto createMypageResponse() {
        return MypageResponseDto.builder()
                .profileImageUrl("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1Y")
                .nickName("성우")
                .email("성우@example.com")
                .userEmailVerified(true)
                .build();
    }
}