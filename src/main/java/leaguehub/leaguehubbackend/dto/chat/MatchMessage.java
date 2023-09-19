package leaguehub.leaguehubbackend.dto.chat;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MatchMessage {

    private Long channelId;

    private String content;

    private Long matchId;

    private String participantId;

    private LocalDateTime timestamp;

    private String type;

}