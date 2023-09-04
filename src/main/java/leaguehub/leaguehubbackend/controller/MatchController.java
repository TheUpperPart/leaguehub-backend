package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leaguehub.leaguehubbackend.dto.match.*;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.match.MatchPlayerService;
import leaguehub.leaguehubbackend.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Match-Controller", description = "대회 경기자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MatchController {

    private final MatchPlayerService matchPlayerService;
    private final MatchService matchService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Operation(summary = "라운드 수(몇 강) 리스트 반환")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "라운드(몇 강) 리스트 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchRoundListDto.class))),
            @ApiResponse(responseCode = "403", description = "매치 결과를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/match/{channelLink}")
    public ResponseEntity loadMatchRoundList(@PathVariable("channelLink") String channelLink) {

        MatchRoundListDto roundList = matchService.getRoundList(channelLink);

        return new ResponseEntity<>(roundList, OK);
    }

    @Operation(summary = "해당 채널의 라운드 경기 배정")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "matchRound", description = "배정 싶은 매치의 라운드(1, 2 라운드)", example = "1, 2, 3, 4")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가자들이 첫 매치에 배정되었습니다."),
            @ApiResponse(responseCode = "403", description = "권한이 관리자가 아님,채널을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/match/{channelLink}/{matchRound}")
    public ResponseEntity assignmentMatches(@PathVariable("channelLink") String channelLink, @PathVariable("matchRound") Integer matchRound) {

        matchService.matchAssignment(channelLink, matchRound);

        return new ResponseEntity<>("참가자들이 첫 매치에 배정되었습니다.", OK);
    }

    @Operation(summary = "해당 채널의 (1, 2, 3)라운드에 대한 매치 조회")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "matchRound", description = "조회하고 싶은 매치의 라운드(1, 2, 3)", example = "1, 2, 3, 4")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치가 조회되었습니다. - 배열로 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchInfoDto.class))),
            @ApiResponse(responseCode = "403", description = "권한이 관리자가 아님,채널을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/match/{channelLink}/{matchRound}")
    public ResponseEntity loadMatchInfo(@PathVariable("channelLink") String channelLink, @PathVariable("matchRound") Integer matchRound) {

        MatchRoundInfoDto matchInfoDtoList = matchService.loadMatchPlayerList(channelLink, matchRound);

        return new ResponseEntity<>(matchInfoDtoList, OK);

    }

    @Operation(summary = "대회에 참여된 플레이어면 좌측 중간에 표시")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치가 조회되었습니다. - 배열로 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchInfoDto.class))),
            @ApiResponse(responseCode = "403", description = "플레이어가 아니면 0 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @MessageMapping("/match/{channelLink}")
    @SendTo("/match/round")
    public ResponseEntity getRoundLive(@PathVariable("channelLink") String channelLink){

        int myMatchRound = matchService.getMyMatchRound(channelLink);

        return new ResponseEntity<>(myMatchRound, OK);

    }

    @MessageMapping("/match/{matchId}/{matchSet}/score-update")
    @SendTo("/match/greetings")
    public ResponseEntity updateMatchPlayerScore(@PathVariable("matchId") Long matchId, @PathVariable("matchSet") Integer matchSet) {
        MatchInfoDto matchInfoDto = matchPlayerService.updateMatchPlayerScore(matchId, matchSet);
        return new ResponseEntity<>(matchInfoDto, OK);
    }

    @MessageMapping("/match/{matchId}")
    @SendTo("/match/greetings")
    public ResponseEntity getMatchInfo(@PathVariable("matchId") Long matchId) {
        MatchInfoDto matchInfo = matchService.getMatchInfo(matchId);

        return new ResponseEntity(matchInfo, OK);
    }


    @MessageMapping("/match/{matchId}")
    @SendTo("/match/{matchId}")
    public List<MatchSetStatusMessage> receiveNote(@DestinationVariable Long matchId, @Payload MatchSetReadyMessage message) {

        matchPlayerService.markPlayerAsReady(message, matchId);

        List<MatchSetStatusMessage> allPlayerStatus = matchPlayerService.getAllPlayerStatusForMatch(matchId);

        return allPlayerStatus;
    }
  
    @GetMapping("/match/{matchId}/player/info")
    public ResponseEntity loadMatchScore(@PathVariable("matchId") Long matchId) {

        MatchScoreInfoDto matchScoreInfoDto = matchService.getMatchScoreInfo(matchId);

        return new ResponseEntity<>(matchScoreInfoDto, OK);
    }

}
