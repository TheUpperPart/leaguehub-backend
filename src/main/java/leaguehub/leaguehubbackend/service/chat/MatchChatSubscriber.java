package leaguehub.leaguehubbackend.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.dto.chat.MatchMessage;
import leaguehub.leaguehubbackend.exception.chat.exception.MatchChatMessageConversionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchChatSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final SimpMessagingTemplate messagingTemplate;

    private static final String MATCH_CHAT_DESTINATION_FORMAT = "/match/%d/chat";


    @Override
    public void onMessage(Message message, byte[] pattern) {
        MatchMessage receivedMessageObj = convertToMatchMessage(message);
        broadcastMessage(receivedMessageObj);
    }

    private MatchMessage convertToMatchMessage(Message message) {
        try {
            return objectMapper.readValue(message.getBody(), MatchMessage.class);
        } catch (IOException e) {
            log.error("message로 변경 실패: {}", e.getMessage());
            throw new MatchChatMessageConversionException();
        }
    }

    private String getDestination(Long matchId) {
        return String.format(MATCH_CHAT_DESTINATION_FORMAT, matchId);
    }

    private void broadcastMessage(MatchMessage message) {
        Long matchId = message.getMatchId();
        String destination = getDestination(matchId);
        messagingTemplate.convertAndSend(destination, message);
    }
}
