package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
public class MatchRank extends BaseTimeEntity {

    @Id
    @Column(name = "match_rank_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer placement;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "match_result_id")
    private MatchResult matchResult;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "match_player_id")
    private MatchPlayer matchPlayer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "match")
    private Match match;

    public static MatchRank createMatchRank(MatchPlayer matchPlayer, Integer placement){
        MatchRank matchRank = new MatchRank();
        matchRank.matchPlayer = matchPlayer;
        matchRank.placement = placement;

        return matchRank;
    }

    public void updateMatchRank(Integer placement) {
        this.placement = placement;
    }
}
