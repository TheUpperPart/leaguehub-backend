package leaguehub.leaguehubbackend.domain.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MyMatchDto {

    @Schema(description = "진행중인 매치 라운드", example = "1, 2, 3 없으면 0")
    Integer myMatchRound;

    @Schema(description = "진행중인 매치 PK", example = "1, 2, 3 없으면 0")
    Long myMatchId;
}
