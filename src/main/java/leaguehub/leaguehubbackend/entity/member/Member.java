package leaguehub.leaguehubbackend.entity.member;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personalId;

    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
    private List<Participant> participant = new ArrayList<>();

    @Builder
    public Member(String personalId, String nickname, String profileImageUrl, LoginProvider loginProvider) {
        this.personalId = personalId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.loginProvider = loginProvider;
    }
}
