package leaguehub.leaguehubbackend.domain.match.entity;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.global.audit.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRank extends BaseTimeEntity {

    @Id
    @Column(name = "match_rank_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_set_id")
    private MatchSet matchSet;

    private String gameId;

    private Integer placement;

    public static MatchRank createMatchRank(MatchSet matchSet,String gameId, Integer placement) {
        MatchRank matchRank = new MatchRank();
        matchRank.matchSet = matchSet;
        matchRank.gameId = gameId;
        matchRank.placement = placement;

        return matchRank;
    }

    public void deleteMatchSet() {
        this.matchSet = null;
    }
}
