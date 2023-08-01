package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class MatchResult extends BaseTimeEntity {

    @Id
    @Column(name = "match_result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer roundRank;

    @Column(name = "match_code")
    private String matchCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "match_id")
    private Match match;

    public static MatchResult createMatchResult(String matchCode,Match match){
        MatchResult matchResult = new MatchResult();
        matchResult.matchCode = matchCode;
        matchResult.match = match;

        return matchResult;
    }
}
