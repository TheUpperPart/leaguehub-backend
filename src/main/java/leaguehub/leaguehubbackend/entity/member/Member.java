package leaguehub.leaguehubbackend.entity.member;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;

import leaguehub.leaguehubbackend.entity.email.EmailAuth;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_auth_id", referencedColumnName = "email_id")
    private EmailAuth emailAuth;

    private boolean emailUserVerified;

    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;

    @Enumerated(EnumType.STRING)
    private BaseRole baseRole;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
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

    public void setEmailAuth(EmailAuth emailAuth) {
        this.emailAuth = emailAuth;
    }

    public void setEmailUserVerified(boolean b) {
        this.emailUserVerified = b;
    }
}
