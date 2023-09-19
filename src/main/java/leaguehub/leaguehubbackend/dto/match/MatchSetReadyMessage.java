package leaguehub.leaguehubbackend.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString
public class MatchSetReadyMessage {
    private Long matchPlayerId;
}
