package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.dto.channel.ParticipantChannelDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Participants-Controller", description = "참가자 관련 API")
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
            @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUserGameInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "게임 ID를 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/participant/stat")
    public ResponseEntity getUserDetail(@RequestParam(value = "gameid") String nickname,
                                        @RequestParam(value = "gamecategory") Integer category) {

        ResponseUserGameInfoDto userDetailDto = participantService.selectGameCategory(nickname, category);

        return new ResponseEntity<>(userDetailDto, OK);
    }

    @Operation(summary = "경기에 참가요청(TFT 만)", description = "관전자가 게임에 참가 요청")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가 요청 확인"),
            @ApiResponse(responseCode = "400", description = "해당 게임에 참여할 수 없는 상태입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/{channelLink}/participant")
    public ResponseEntity participateMatch(@PathVariable("channelLink") String channelLink, @RequestBody @Valid ParticipantDto responseDto) {

        participantService.participateMatch(responseDto, channelLink);

        return new ResponseEntity<>("Update Participant ROLE", OK);
    }


    @Operation(summary = "게임 참가자(Player) 조회 ", description = "채널 내 게임 참가자(Player) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{channelLink}/players")
    public ResponseEntity loadPlayer(@PathVariable("channelLink") String channelLink) {


        List<ResponseStatusPlayerDto> players = participantService.loadPlayers(channelLink);

        return new ResponseEntity<>(players, OK);
    }


    @Operation(summary = "게임 참가요청자(Request) 조회 ", description = "채널 내 게임 참가요청자(Request) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 채널의 관리자가 아닙니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{channelLink}/player/requests")
    public ResponseEntity loadRequestPlayer(@PathVariable("channelLink") String channelLink) {

        List<ResponseStatusPlayerDto> responsePlayers = participantService.loadRequestStatusPlayerList(channelLink);

        return new ResponseEntity<>(responsePlayers, OK);
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
    @PostMapping("/{channelLink}/{participantId}/player")
    public ResponseEntity approveParticipant(@PathVariable("channelLink") String channelLink,
                                             @PathVariable("participantId") Long participantId) {

        participantService.approveParticipantRequest(channelLink, participantId);

        return new ResponseEntity<>("approve participant", OK);
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
    @PostMapping("/{channelLink}/{participantId}/observer")
    public ResponseEntity rejectedParticipant(@PathVariable("channelLink") String channelLink,
                                              @PathVariable("participantId") Long participantId) {

        participantService.rejectedParticipantRequest(channelLink, participantId);

        return new ResponseEntity<>("reject participant", OK);
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
    @PostMapping("/{channelLink}/{participantId}/host")
    public ResponseEntity updateHostParticipant(@PathVariable("channelLink") String channelLink,
                                                @PathVariable("participantId") Long participantId) {

        participantService.updateHostRole(channelLink, participantId);

        return new ResponseEntity<>("update HOST", OK);
    }

    @Operation(summary = "채널 관전자(Observer) 조회 ", description = "채널 내 게임 관전자(Observer) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관전자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 채널의 관리자가 아닙니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{channelLink}/observers")
    public ResponseEntity loadObserverPlayer(@PathVariable("channelLink") String channelLink) {

        List<ResponseStatusPlayerDto> responsePlayers = participantService.loadObserverPlayerList(channelLink);


        return new ResponseEntity<>(responsePlayers, OK);
    }

    @Operation(summary = "채널 참가", description = "채널 링크를 통하여 해당 채널 참가")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그 채널의 정보 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantChannelDto.class))),
            @ApiResponse(responseCode = "403", description = "해당 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/{channelLink}/participant/observer")
    public ResponseEntity participateChannel(@PathVariable("channelLink") String channelLink) {

        Participant participant = participantService.participateChannel(channelLink);

        return new ResponseEntity<>(participant.getNickname(), OK);
    }

    @Operation(summary = "채널 나가기", description = "채널 링크를 통하여 해당 채널 나가기")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널을 나갔습니다."),
            @ApiResponse(responseCode = "403", description = "해당 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @DeleteMapping("/{channelLink}")
    public ResponseEntity leaveChannel(@PathVariable("channelLink") String channelLink) {

        participantService.leaveChannel(channelLink);

        return new ResponseEntity<>("Leave this Channel", OK);
    }


    @Operation(summary = "채널 순서를 커스텀하게 구성 - 로그인시 사이드바 화면 구성을 커스텀할 수 있음",
            description = "입력과 반환 Dto가 동일하게 되어서 API요청 필요 없이 바로 회면 구성 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dto를 리스트로 반환",content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantChannelDto.class))),
    })
    @PostMapping("/channels/order")
    public ResponseEntity updateCustomChannelIndex(@RequestBody List<ParticipantChannelDto> participantChannelDtoList) {

        List<ParticipantChannelDto> updateCustomChannelIndexList = participantService.updateCustomChannelIndex(participantChannelDtoList);

        return new ResponseEntity<>(updateCustomChannelIndexList, OK);
    }


}
