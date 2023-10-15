package leaguehub.leaguehubbackend.dto.chat;

import leaguehub.leaguehubbackend.entity.chat.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchMessage {

    private String channelLink;

    private String content;

    private Long matchId;

    private Long participantId;

    private LocalDateTime timestamp;

    private MessageType type;

}