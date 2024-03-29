package leaguehub.leaguehubbackend.domain.member.entity;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.domain.email.entity.EmailAuth;
import leaguehub.leaguehubbackend.domain.member.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.global.audit.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Entity
@DynamicUpdate
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personalId;

    @Column(length = 20)
    private String nickname;

    private String profileImageUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_auth_id", referencedColumnName = "email_id")
    private EmailAuth emailAuth;

    private boolean emailUserVerified;

    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;

    @Enumerated(EnumType.STRING)
    private BaseRole baseRole;

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
    public void assignEmailAuth(EmailAuth emailAuth) {
        this.emailAuth = emailAuth;
    }
    public void verifyEmail() {
        this.emailUserVerified = true;
    }
    public void unverifyEmail() {
        this.emailUserVerified = false;
    }
    public void updateNickname(String newNickname) { this.nickname = newNickname; }
    public void updateProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
