package leaguehub.leaguehubbackend.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GameResultDto {

    @Schema(description = "몇번째 매치 세트인지 나타낸다. 매치의 첫번째 매치세트 즉 3세트까지 있으면 1, 2, 3이 반환된다.", example = "1, 2, 3")
    private Integer matchSetCount;

    @Schema(description = "플레이어 아이디와 등수과 담겨져있다.")
    private List<MatchRankResultDto> matchRankResultDtos = new ArrayList<>();

    @Builder
    public GameResultDto(Integer matchSetCount, List<MatchRankResultDto> matchRankResultDtos) {
        this.matchSetCount = matchSetCount;
        this.matchRankResultDtos = matchRankResultDtos;
    }
}
