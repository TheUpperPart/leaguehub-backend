package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class MatchRank extends BaseTimeEntity {

    @Id
    @Column(name = "match_rank_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String participant;

    private String placement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_result_id")
    private MatchResult matchResult;
}
