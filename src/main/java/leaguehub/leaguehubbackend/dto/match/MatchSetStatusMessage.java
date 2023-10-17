package leaguehub.leaguehubbackend.dto.match;

import leaguehub.leaguehubbackend.entity.match.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@ToString
public class MatchSetStatusMessage {
    private Long playerId;
    private PlayerStatus status;
}