package leaguehub.leaguehubbackend.dto.chat;

import leaguehub.leaguehubbackend.entity.chat.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MatchMessage {

    private Long channelId;

    private String content;

    private Long matchId;

    private Long participantId;

    private LocalDateTime timestamp;

    private MessageType type;

}