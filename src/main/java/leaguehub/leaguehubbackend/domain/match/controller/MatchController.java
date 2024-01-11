package leaguehub.leaguehubbackend.domain.match.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leaguehub.leaguehubbackend.domain.match.dto.*;
import leaguehub.leaguehubbackend.domain.match.service.MatchPlayerService;
import leaguehub.leaguehubbackend.domain.match.service.MatchService;
import leaguehub.leaguehubbackend.domain.match.service.chat.MatchChatService;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantIdResponseDto;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
    private final MatchChatService matchChatService;
    @Operation(summary = "라운드 수(몇 강) 리스트 반환 - 사용자")
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
            @ApiResponse(responseCode = "200", description = "매치가 조회되었습니다. - 배열로 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchRoundInfoDto.class))),
            @ApiResponse(responseCode = "403", description = "권한이 관리자가 아님,채널을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/match/{channelLink}/{matchRound}")
    public ResponseEntity loadMatchInfo(@PathVariable("channelLink") String channelLink, @PathVariable("matchRound") Integer matchRound) {

        MatchRoundInfoDto matchInfoDtoList = matchService.loadMatchPlayerList(channelLink, matchRound);

        return new ResponseEntity<>(matchInfoDtoList, OK);

    }



    @MessageMapping("/match/{matchId}/{matchSet}/score-update")
    public void updateMatchPlayerScore(@DestinationVariable("matchId") String matchIdStr, @DestinationVariable("matchSet") String matchSetStr) {
        Long matchId = Long.valueOf(matchIdStr);
        Integer matchSet = Integer.valueOf(matchSetStr);
        long endTime = System.currentTimeMillis() / 1000;
        MatchInfoDto matchInfoDto = matchPlayerService.updateMatchPlayerScore(matchId, matchSet, endTime);

        simpMessagingTemplate.convertAndSend("/match/" + matchId + "/" + matchSet, matchInfoDto);
    }


    @MessageMapping("/match/{matchId}/checkIn")
    public void checkIn(@DestinationVariable("matchId") String matchIdStr, @Payload MatchSetReadyMessage message) {

        ParticipantIdResponseDto participantIdResponseDto = matchPlayerService.markPlayerAsReady(message, matchIdStr);

        simpMessagingTemplate.convertAndSend("/match/" + matchIdStr, participantIdResponseDto);
    }

    @Operation(summary = "현재 진행중인 매치의 정보 조회.")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "matchId", description = "조회 대상 matchId", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치가 조회됨", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchScoreInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "매치를 찾지 못함", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}/match/{matchId}/player/info")
    public ResponseEntity loadMatchScore(@PathVariable("channelLink") String channelLink, @PathVariable("matchId") Long matchId) {

        MatchScoreInfoDto matchScoreInfoDto = matchService.getMatchScoreInfo(channelLink, matchId);

        List<MatchMessage> matchMessages = matchChatService.findMatchChatHistory(channelLink, matchId);

        matchScoreInfoDto.setMatchMessages(matchMessages);

        return new ResponseEntity<>(matchScoreInfoDto, OK);
    }

    @Operation(summary = "해당 채널의 (1, 2, 3)라운드에 대한 경기 횟수 설정")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "roundCountList", description = "설정할려는 횟수 배열", example = "[3, 4, 2, 1]")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경기 횟수가 배정되었습니다."),
            @ApiResponse(responseCode = "403", description = "매치 또는 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/match/{channelLink}/count")
    public ResponseEntity setMatchRoundCount(@PathVariable("channelLink") String channelLink,
                                             @RequestBody MatchSetCountDto matchSetCountDto) {

        matchService.setMatchSetCount(channelLink, matchSetCountDto.getMatchSetCountList());

        return new ResponseEntity("경기 횟수가 배정되었습니다.", OK);
    }

    @Operation(summary = "해당 채널의 (1, 2, 3)라운드에 대한 설정된 경기 횟수를 반환")
    @Parameter(name = "roundCountList", description = "설정할려는 횟수 배열 결승전부터", example = "[3, 4, 2, 1]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경기 횟수 반환"),
            @ApiResponse(responseCode = "403", description = "매치 또는 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("match/{channelLink}/count")
    public ResponseEntity getMatchRoundCount(@PathVariable("channelLink") String channelLink) {

        MatchSetCountDto matchSetCountDto = matchService.getMatchSetCount(channelLink);

        return new ResponseEntity(matchSetCountDto, OK);
    }

    @Operation(summary = "해당 채널 매치의 결과 - 이전 경기 결과를 가져옴(Mongo 형식) 매치 세트 결과를 다 가져온다.")
    @Parameters(value = {
            @Parameter(name = "matchId", description = "불러오고 싶은 매치의 PK", example = "3"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치 결과를 리스트로 가져온다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameResultDto.class))),
            @ApiResponse(responseCode = "404", description = "매치 세트를 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/match/{matchId}/result")
    public ResponseEntity getGameResult(@PathVariable Long matchId) {
        List<GameResultDto> gameResultList = matchPlayerService.getGameResult(matchId);

        return new ResponseEntity(gameResultList, OK);
    }

    @MessageMapping("/match/{channelLink}/{participantId}/{matchId}/call-admin")
    public void callAdmin(@DestinationVariable("channelLink") String channelLink,
                          @DestinationVariable("participantId") String participantId,
                          @DestinationVariable("matchId") String matchId) {

        MatchCallAdminDto matchCallAdminDto = matchService.callAdmin(channelLink, Long.valueOf(matchId), Long.valueOf(participantId));

        matchChatService.processAdminAlert(channelLink, Long.valueOf(matchId));

        simpMessagingTemplate.convertAndSend("/match/" + channelLink, matchCallAdminDto);
    }


    @Operation(summary = "해당 매치 알람 끄기")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "matchId", description = "알람을 끄는 match의 PK", example = "1, 2, 3, 4")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "권한이 관리자가 아님,채널을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/match/{channelLink}/{matchId}/call-off")
    public ResponseEntity turnOffAlarm(@PathVariable("channelLink") String channelLink,
                                       @PathVariable("matchId") Long matchId){

        matchService.turnOffAlarm(channelLink, matchId);

        return new ResponseEntity(OK);
    }

}
