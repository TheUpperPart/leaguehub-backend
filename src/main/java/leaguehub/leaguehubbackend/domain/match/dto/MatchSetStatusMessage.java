package leaguehub.leaguehubbackend.domain.match.dto;

import leaguehub.leaguehubbackend.domain.match.entity.PlayerStatus;
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