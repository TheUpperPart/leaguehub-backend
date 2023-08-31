package leaguehub.leaguehubbackend.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import leaguehub.leaguehubbackend.entity.match.MatchStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MatchInfoDto {

    @Schema(description = "매치 이름", example = "Group A")
    private String matchName;

    @Schema(description = "해당 매치의 상세보기 or 체크인하기 위한 매치 링크", example = "1")
    private Long matchId;

    @Schema(description = "매치 상태", example = "대기 | 경기 중 | 경기 종료")
    private MatchStatus matchStatus;

    @Schema(description = "몇 강", example = "64(강), 32(강), 16(강)")
    private Integer matchRound;

    @Schema(description = "해당 매치 경기 현재 횟수", example = "1(회)")
    private Integer matchRoundCount;

    @Schema(description = "해당 매치 최대 경기 횟수", example = "3(회)")
    private Integer matchRoundMaxCount;

    @Schema(description = "매치에 속해있는 플레이어의 정보", example = "배열로 반환")
    private List<MatchPlayerInfo> matchPlayerInfoList;

    @Builder
    public MatchInfoDto(String matchName, Long matchId, MatchStatus matchStatus, Integer matchRound, Integer matchRoundCount, Integer matchRoundMaxCount, List<MatchPlayerInfo> matchPlayerInfoList) {
        this.matchName = matchName;
        this.matchId = matchId;
        this.matchStatus = matchStatus;
        this.matchRound = matchRound;
        this.matchRoundCount = matchRoundCount;
        this.matchRoundMaxCount = matchRoundMaxCount;
        this.matchPlayerInfoList = matchPlayerInfoList;
    }
}
