package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.chat.MatchMessage;
import leaguehub.leaguehubbackend.service.chat.MatchChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchChatController {

    private final MatchChatService matchChatService;

    @MessageMapping("/match/chat")
    public void sendMessage(@Payload MatchMessage message) {
        matchChatService.processMessage(message);
    }

}
