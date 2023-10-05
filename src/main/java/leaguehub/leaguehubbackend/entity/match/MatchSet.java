package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class MatchSet extends BaseTimeEntity {

    @Id
    @Column(name = "match_set_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Column(unique = true, name = "riot_match_uuid")
    private String riotMatchUuid;

    private Boolean updateScore;

    private Integer setCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matchSet")
    List<MatchRank> matchRankList = new ArrayList<>();

    public void updateRiotMatchUuid(String riotMatchUuid) {
        this.riotMatchUuid = riotMatchUuid;
    }

    public void updateScore(boolean updateScore) {
        this.updateScore = updateScore;
    }

    public static MatchSet createMatchSet(Match match, Integer setCount){
        MatchSet matchSet = new MatchSet();
        matchSet.match = match;
        matchSet.updateScore = false;
        matchSet.setCount = setCount;

        return matchSet;
    }
}
