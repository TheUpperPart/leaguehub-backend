package leaguehub.leaguehubbackend.domain.participant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leaguehub.leaguehubbackend.domain.participant.dto.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantQueryService;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Participants-Query-Controller", description = "참가자 조회 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantQueryController {

    private final ParticipantQueryService participantQueryService;


    @Operation(summary = "티어 검색 (참가 x)", description = "검색 버튼을 누르면 해당 카테고리와 게임 닉네임을 가지고 티어 검색 (참가 x)")
    @Parameters(value = {
            @Parameter(name = "gameid", description = "해당 게임 닉네임", example = "칸영기"),
            @Parameter(name = "gamecategory", description = "게임 종류 (tft, lol, ...)", example = "0, 1, 2")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUserGameInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "게임 ID를 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/participant/stat/{gameId}/{gameTag}")
    public ResponseEntity getTFTRanked(@PathVariable("gameId") String gameId, @PathVariable("gameTag") String gameTag){

        ResponseUserGameInfoDto userDetailDto = participantQueryService.selectGameCategory(gameId + "#" + gameTag, 0);

        return new ResponseEntity<>(userDetailDto, OK);
    }


    @Operation(summary = "게임 참가자(Player) 조회 ", description = "채널 내 게임 참가자(Player) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{channelLink}/players")
    public ResponseEntity getPlayers(@PathVariable("channelLink") String channelLink){

        List<ResponseStatusPlayerDto> players = participantQueryService.loadPlayers(channelLink);

        return new ResponseEntity<>(players, OK);
    }

    @Operation(summary = "게임 참가요청자(Request) 조회 ", description = "채널 내 게임 참가요청자(Request) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 채널의 관리자가 아닙니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{channelLink}/player/requests")
    public ResponseEntity getRequestPlayers(@PathVariable("channelLink") String channelLink){

        List<ResponseStatusPlayerDto> responsePlayers = participantQueryService.loadRequestStatusPlayerList(channelLink);

        return new ResponseEntity<>(responsePlayers, OK);
    }

    @Operation(summary = "채널 관전자(Observer) 조회 ", description = "채널 내 게임 관전자(Observer) 모두 조회")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관전자 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatusPlayerDto.class))),
            @ApiResponse(responseCode = "400", description = "해당 채널의 관리자가 아닙니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/{channelLink}/observers")
    public ResponseEntity getObserverPlayer(@PathVariable("channelLink") String channelLink){

        List<ResponseStatusPlayerDto> responsePlayers = participantQueryService.loadObserverPlayerList(channelLink);

        return new ResponseEntity<>(responsePlayers, OK);
    }




}
