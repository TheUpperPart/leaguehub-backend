package leaguehub.leaguehubbackend.dto.match;


import leaguehub.leaguehubbackend.entity.match.PlayerStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchPlayerScoreInfo {

    private Long matchPlayerId;

    private Long participantId;

    private Integer matchRank;

    private String participantImageUrl;

    private String participantGameId;

    private Integer playerScore;

    private PlayerStatus playerStatus;

}
