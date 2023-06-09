package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class MatchPlayer extends BaseTimeEntity {

    @Id
    @Column(name = "match_player_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer playerScore;

    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;
}
