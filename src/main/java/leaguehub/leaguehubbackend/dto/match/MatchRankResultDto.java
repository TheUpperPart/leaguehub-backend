package leaguehub.leaguehubbackend.dto.match;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MatchRankResultDto {

    private String gameId;

    private Integer placement;

    public MatchRankResultDto(String gameId, Integer placement) {
        this.gameId = gameId;
        this.placement = placement;
    }
}
