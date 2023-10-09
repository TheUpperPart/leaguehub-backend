package leaguehub.leaguehubbackend.dto.match;

import leaguehub.leaguehubbackend.dto.chat.MatchMessage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchScoreInfoDto {
    private Long requestMatchPlayerId;

    private Integer matchRound;

    private Integer matchCurrentSet;

    private Integer matchSetCount;


    private List<MatchPlayerInfo> matchPlayerInfos;

    private List<MatchMessage> matchMessages;


}
