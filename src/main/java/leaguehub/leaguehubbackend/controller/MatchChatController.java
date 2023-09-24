package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.chat.MatchMessage;
import leaguehub.leaguehubbackend.service.chat.MatchChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MatchChatController {

    private final MatchChatService matchChatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final String MATCH_CHAT_DESTINATION_FORMAT = "/match/%s/chat/history";

    @MessageMapping("/match/chat")
    public void sendMessage(@Payload MatchMessage message) {
        matchChatService.processMessage(message);
    }

    @MessageMapping("/channel/{channelId}/match/{matchId}/chat/history")
    public void getMatchChatHistory(@DestinationVariable("matchId") String channelIdStr, @DestinationVariable("matchId") String matchIdStr) {

        List<MatchMessage> matchMessages = matchChatService.findMatchChatHistory(channelIdStr, matchIdStr);

        String matchChat = String.format(MATCH_CHAT_DESTINATION_FORMAT, matchIdStr);

        simpMessagingTemplate.convertAndSend(matchChat, matchMessages);
    }

}
