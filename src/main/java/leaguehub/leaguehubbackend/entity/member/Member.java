package leaguehub.leaguehubbackend.entity.member;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personalId;

    private String nickname;

    private String profileImageUrl;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;

    @Enumerated(EnumType.STRING)
    private BaseRole baseRole;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static Member kakaoUserToMember(KakaoUserDto kakaoUserDto) {
        return Member.builder()
                .personalId(String.valueOf(kakaoUserDto.getId()))
                .nickname(kakaoUserDto.getProperties().getNickname())
                .profileImageUrl(kakaoUserDto.getProperties().getProfileImage())
                .baseRole(BaseRole.USER)
                .loginProvider(LoginProvider.KAKAO)
                .build();
    }
}
