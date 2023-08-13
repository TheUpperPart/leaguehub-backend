package leaguehub.leaguehubbackend.dto.participant;

import leaguehub.leaguehubbackend.entity.participant.GameTier;
import lombok.Data;

@Data
public class GameRankDto {

    private GameTier gameRank;

    private String gameGrade;
}
