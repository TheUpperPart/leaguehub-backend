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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

}
