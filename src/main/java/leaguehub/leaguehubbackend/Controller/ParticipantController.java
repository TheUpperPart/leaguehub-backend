package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Participants", description = "참가자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantController {

    private final ParticipantService participantService;


    @Operation(summary = "티어 검색 (참가 x)", description = "검색 버튼을 누르면 해당 카테고리와 게임 닉네임을 가지고 티어 검색 (참가 x)")
    @Parameters(value = {
            @Parameter(name = "gameid", description = "해당 게임 닉네임", example = "칸영기"),
            @Parameter(name = "gamecategory", description = "게임 종류 (tft, lol, ...)", example = "0, 1, 2")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUserDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "게임 ID를 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/stat")
    public ResponseEntity getUserDetail(@RequestParam(value = "gameid") String nickname,
                                        @RequestParam(value = "gamecategory") Integer category) {

        ResponseUserDetailDto userDetailDto = participantService.selectGameCategory(nickname, category);

        return new ResponseEntity<>(userDetailDto, HttpStatus.OK);
    }


    @Operation(summary = "관전자 확인", description = "참가 버튼을 누르면 관전자인지 확인하는 주소")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관전자 확인"),
            @ApiResponse(responseCode = "400", description = "해당 게임에 참여할 수 없는 상태입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/participant/{channelLink}")
    public ResponseEntity participateMatch(@PathVariable("channelLink") String channelLink) {

        participantService.checkParticipateMatch(channelLink);

        return new ResponseEntity<>("Valid OBSERVER ROLE", HttpStatus.OK);
    }


    @Operation(summary = "경기에 참가요청(TFT 만)", description = "관전자가 게임에 참가 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가 요청 확인"),
            @ApiResponse(responseCode = "400", description = "해당 게임에 참여할 수 없는 상태입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/participant/match")
    public ResponseEntity participateMatch(@RequestBody ParticipantResponseDto responseDto) {

        participantService.participateMatch(responseDto);

        return new ResponseEntity<>("Update Participant ROLE", HttpStatus.OK);
    }


    @Operation(summary = "게임 참가자(Player) 조회 ", description = "채널 내 게임 참가자(Player) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/profile/player")
    public ResponseEntity loadPlayer(@RequestParam(value = "channelLink") String channelLink) {


        List<ResponseStatusPlayerDto> players = participantService.loadPlayers(channelLink);

        return new ResponseEntity<>(players, HttpStatus.OK);
    }


    @Operation(summary = "게임 참가요청자(Request) 조회 ", description = "채널 내 게임 참가요청자(Request) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 채널의 관리자가 아닙니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/profile/request")
    public ResponseEntity loadRequestPlayer(@RequestParam(value = "channelLink") String channelLink) {

        List<ResponseStatusPlayerDto> responsePlayers = participantService.loadRequestStatusPlayerList(channelLink);

        return new ResponseEntity<>(responsePlayers, HttpStatus.OK);
    }

    @Operation(summary = "참가요청 승인", description = "관리자가 해당 게임 참가요청자(request)를 승인")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "participantId", description = "해당 채널 참가자의 고유 Id", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공"),
            @ApiResponse(responseCode = "404", description = "채널 참가자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/player/approve/{channelLink}/{participantId}")
    public ResponseEntity approveParticipant(@PathVariable("channelLink") String channelLink,
                                             @PathVariable("participantId") Long participantId) {

        participantService.approveParticipantRequest(channelLink, participantId);

        return new ResponseEntity<>("approve participant", HttpStatus.OK);
    }

    @Operation(summary = "참가요청 거절", description = "관리자가 해당 게임 참가요청자(request)를 거절")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "participantId", description = "해당 채널 참가자의 고유 Id", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "거절 성공"),
            @ApiResponse(responseCode = "404", description = "채널 참가자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/player/reject/{channelLink}/{participantId}")
    public ResponseEntity rejectedParticipant(@PathVariable("channelLink") String channelLink,
                                              @PathVariable("participantId") Long participantId) {

        participantService.rejectedParticipantRequest(channelLink, participantId);

        return new ResponseEntity<>("reject participant", HttpStatus.OK);
    }

    @Operation(summary = "관리자 권한 부여", description = "관리자가 관전자에게 권한을 부여")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "participantId", description = "해당 채널 참가자의 고유 Id", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 권한 부여 성공"),
            @ApiResponse(responseCode = "404", description = "채널 참가자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/player/host/{channelLink}/{participantId}")
    public ResponseEntity updateHostParticipant(@PathVariable("channelLink") String channelLink,
                                                @PathVariable("participantId") Long participantId) {

        participantService.updateHostRole(channelLink, participantId);

        return new ResponseEntity<>("update HOST", HttpStatus.OK);
    }

    @Operation(summary = "채널 관전자(Observer) 조회 ", description = "채널 내 게임 관전자(Observer) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관전자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 채널의 관리자가 아닙니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/profile/observer")
    public ResponseEntity loadObserverPlayer(@RequestParam(value = "channelLink") String channelLink) {

        List<ResponseStatusPlayerDto> responsePlayers = participantService.loadObserverPlayerList(channelLink);


        return new ResponseEntity<>(responsePlayers, HttpStatus.OK);
    }


}
