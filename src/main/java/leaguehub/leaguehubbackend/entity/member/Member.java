package leaguehub.leaguehubbackend.entity.member;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private LoginProvider loginProvider;

}
