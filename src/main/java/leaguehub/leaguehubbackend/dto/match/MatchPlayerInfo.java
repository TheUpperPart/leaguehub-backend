package leaguehub.leaguehubbackend.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import leaguehub.leaguehubbackend.entity.match.MatchPlayerResultStatus;
import leaguehub.leaguehubbackend.entity.match.PlayerStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MatchPlayerInfo {

    @Schema(description = "플레이어의 matchPlayerId", example = "1")
    private Long matchPlayerId;

    @Schema(description = "플레이어의 ParticipantId", example = "2")
    private Long participantId;

    @Schema(description = "플레이어의 게임 닉네임", example = "돈절래")
    private String gameId;

    @Schema(description = "플레이어의 게임 티어", example = "Diamond II")
    private String gameTier;

    @Schema(description = "플레이어의 체크인 상태", example = "READY, WAITING")
    private PlayerStatus playerStatus;

    @Schema(description = "참가자 점수", example = "8(점), 5(점), ...")
    private Integer score;

    @Schema(description = "참가자 프로필 이미지 주소", example = "https://league.s3.ap-northeast-2.amazonaws.com/imgSrc.png")
    private String profileSrc;

    @Schema(description = "매치 결과 상태", example = "진행중 | 탈락 | 다음 라운드로 진출 | 실격")
    private MatchPlayerResultStatus matchPlayerResultStatus;

    @Builder
    public MatchPlayerInfo(Long matchPlayerId, Long participantId, String gameId, String gameTier, PlayerStatus playerStatus, Integer score, MatchPlayerResultStatus matchPlayerResultStatus, String profileSrc) {
        this.matchPlayerId = matchPlayerId;
        this.participantId = participantId;
        this.gameId = gameId;
        this.gameTier = gameTier;
        this.playerStatus = playerStatus;
        this.score = score;
        this.matchPlayerResultStatus = matchPlayerResultStatus;
        this.profileSrc = profileSrc;
    }
}
