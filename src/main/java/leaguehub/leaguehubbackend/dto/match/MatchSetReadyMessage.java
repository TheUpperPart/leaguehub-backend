package leaguehub.leaguehubbackend.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@ToString
public class MatchSetReadyMessage {
    private Long matchPlayerId;
}
