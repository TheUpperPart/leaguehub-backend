package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

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

    @Enumerated(EnumType.STRING)
    private MatchPlayerResultStatus matchPlayerResultStatus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    public static MatchPlayer createMatchPlayer(Participant participant, Match match){
        MatchPlayer matchPlayer = new MatchPlayer();
        matchPlayer.playerStatus = PlayerStatus.WAITING;
        matchPlayer.matchPlayerResultStatus = MatchPlayerResultStatus.PROGRESS;
        matchPlayer.participant = participant;
        matchPlayer.playerScore = 0;
        matchPlayer.match = match;

        return matchPlayer;
    }

    public void updateMatchPlayerScore(Integer placement) {
        this.playerScore += 9 - placement;
    }

    public void updatePlayerCheckInStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public void updateMatchPlayerScoreDisqualified(){
        this.playerScore = -1;
    }

    public void updateMatchPlayerResultStatus(MatchPlayerResultStatus matchPlayerResultStatus) {
        this.matchPlayerResultStatus = matchPlayerResultStatus;
    }
}
