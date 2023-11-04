package leaguehub.leaguehubbackend.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchMessage {

    private String channelLink;

    private String content;

    private Long matchId;

    private Long participantId;

    private String adminName;

    private String accessToken;

    private LocalDateTime timestamp;

    private MessageType type;

}