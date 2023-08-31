package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class MatchSet {

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

    public void updateRiotMatchUuid(String riotMatchUuid) {
        this.riotMatchUuid = riotMatchUuid;
    }

    public void updateScore(boolean updateScore) {
        this.updateScore = updateScore;
    }

}
