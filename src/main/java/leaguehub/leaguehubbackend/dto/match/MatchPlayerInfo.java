package leaguehub.leaguehubbackend.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import leaguehub.leaguehubbackend.entity.match.PlayerStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MatchPlayerInfo {

    @Schema(description = "플레이어의 게임 닉네임", example = "돈절래")
    private String gameId;

    @Schema(description = "플레이어의 게임 티어", example = "Diamond II")
    private String gameTier;

    @Schema(description = "플레이어의 경기 상태", example = "대기 | 경기 중 | 실격 | 탈락 | 승급 | 우승")
    private PlayerStatus playerStatus;

    @Schema(description = "참가자 점수", example = "8(점), 5(점), ...")
    private Integer score;


    @Builder
    public MatchPlayerInfo(String gameId, String gameTier, PlayerStatus playerStatus, Integer score) {
        this.gameId = gameId;
        this.gameTier = gameTier;
        this.playerStatus = playerStatus;
        this.score = score;
    }
}
