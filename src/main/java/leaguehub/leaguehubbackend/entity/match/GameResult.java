package leaguehub.leaguehubbackend.entity.match;

import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "game_result")
@Getter
@NoArgsConstructor
public class GameResult{

    @Id
    private Long id;

    private List<MatchRankResultDto> matchRankResult;

    @CreatedDate
    private LocalDateTime created_date;

    @LastModifiedDate
    private LocalDateTime modified_date;


    public static GameResult createGameResult(Long id, List<MatchRankResultDto> matchRankResult) {
        GameResult gameResult = new GameResult();
        gameResult.id = id;
        gameResult.matchRankResult = matchRankResult;

        return gameResult;
    }
}
