package leaguehub.leaguehubbackend.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MatchResponseDto {

    @Schema(description = "조회하는 매치 링크", example = "42abs121ab88")
    String matchLink;

    @Schema(description = "버튼을 누르는 사람의 gameId", example = "숙자인소환사")
    String nickName;
}
