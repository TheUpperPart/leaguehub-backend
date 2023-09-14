package leaguehub.leaguehubbackend.entity.match;

import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "game_result")
@Getter
@NoArgsConstructor
public class GameResult extends BaseTimeEntity {

    @Id
    private Long id;

    private List<MatchRankResultDto> matchRankResult;

    public static GameResult createGameResult(Long id, List<MatchRankResultDto> matchRankResult) {
        GameResult gameResult = new GameResult();
        gameResult.id = id;
        gameResult.matchRankResult = matchRankResult;

        return gameResult;
    }
}
