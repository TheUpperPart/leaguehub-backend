package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import leaguehub.leaguehubbackend.dto.chat.MatchMessage;
import leaguehub.leaguehubbackend.service.chat.MatchChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MatchChatController {

    private final MatchChatService matchChatService;

    @MessageMapping("/match/chat")
    public void sendMessage(@Payload MatchMessage message) {
        matchChatService.processMessage(message);
    }

    @Operation(summary = "관리자 페이지에서 매치 채팅 내역 조회")
    @Parameters(value = {
            @Parameter(name = "channelId", description = "채널 id", example = "1"),
            @Parameter(name = "matchId", description = "매치 id", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치 채팅 내역 조회성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchMessage.class))),
    })
    @PostMapping("/api/channelLink/{channelLink}/match/{matchId}/chat/history")
    public List<MatchMessage> getMatchChatHistory(@PathVariable("channelLink") String channelLink, @PathVariable("matchId") Long matchId) {

        return matchChatService.findMatchChatHistory(channelLink, matchId);
    }

}
