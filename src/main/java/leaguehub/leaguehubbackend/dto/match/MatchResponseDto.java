package leaguehub.leaguehubbackend.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MatchResponseDto {

    @Schema(description = "조회하는 매치 Id", example = "1")
    private Long matchId;

    @Schema(description = "버튼을 누르는 사람의 gameId", example = "숙자인소환사")
    private String gameId;
}
