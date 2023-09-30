package leaguehub.leaguehubbackend.dto.match;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchScoreInfoDto {
    private String requestMatchPlayerId;

    private List<MatchPlayerInfo> matchPlayerInfos;
}
