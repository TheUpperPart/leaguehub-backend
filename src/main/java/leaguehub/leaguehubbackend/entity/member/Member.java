package leaguehub.leaguehubbackend.entity.member;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;

import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personalId;

    private String nickname;

    private String profileImageUrl;

    private String refreshToken;

    private String email;

    private boolean emailUserVerified;

    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;

    @Enumerated(EnumType.STRING)
    private BaseRole baseRole;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void updateEmail(String email) { this.email = email; }
    public void updateRole(BaseRole role) { this.baseRole = role; }

    public static Member kakaoUserToMember(KakaoUserDto kakaoUserDto) {
        return Member.builder()
                .personalId(String.valueOf(kakaoUserDto.getId()))
                .nickname(kakaoUserDto.getProperties().getNickname())
                .profileImageUrl(kakaoUserDto.getProperties().getProfileImage())
                .emailUserVerified(false)
                .baseRole(BaseRole.GUEST)
                .loginProvider(LoginProvider.KAKAO)
                .build();

    }
}
