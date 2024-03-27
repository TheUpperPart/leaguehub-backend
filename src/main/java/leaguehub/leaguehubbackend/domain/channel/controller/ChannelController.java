package leaguehub.leaguehubbackend.domain.channel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.domain.channel.dto.*;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelStatusAlreadyException;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelDeleteService;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantNotGameHostException;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantQueryService;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantService;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final ParticipantService participantService;
    private final ChannelDeleteService channelDeleteService;
    private final ParticipantQueryService participantQueryService;

    @Operation(summary = "채널 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantChannelDto.class))),
            @ApiResponse(responseCode = "400", description = "Dto 유효성 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel")
    public ResponseEntity createChannel(@Valid @RequestBody CreateChannelDto createChannelDto) {

        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);

        return new ResponseEntity<>(participantChannelDto, OK);
    }

    @Operation(summary = "채널 가져오기 - 단일 채널(화면 구성)")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseChannelDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}")
    public ResponseEntity getChannel(@PathVariable("channelLink") String channelLink) {

        ChannelDto channelInfo = channelService.findChannel(channelLink);

        ResponseChannelDto responseChannelDto = ResponseChannelDto.builder()
                .gameCategory(channelInfo.getGameCategory().getNum())
                .hostName(participantQueryService.findChannelHost(channelLink))
                .participateNum(channelInfo.getRealPlayer())
                .maxPlayer(channelInfo.getMaxPlayer())
                .leagueTitle(channelInfo.getTitle())
                .permission(participantQueryService.findParticipantPermission(channelLink))
                .build();

        return new ResponseEntity(responseChannelDto, OK);
    }

    @Operation(summary = "채널 가져오기 - 여러 채널(로그인시 사이드바 화면 구성)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dto를 리스트로 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantChannelDto.class))),
    })
    @GetMapping("/channels")
    public ResponseEntity loadChannels() {

        List<ParticipantChannelDto> participantChannelList = channelService.findParticipantChannelList();

        return new ResponseEntity(participantChannelList, OK);
    }


    @Operation(summary = "채널 업데이트  - 채널이름, 최대 참가자 수, 채널 이미지)")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}")
    public ResponseEntity updateChannel(@PathVariable("channelLink") String channelLink,
                                        @RequestBody UpdateChannelDto updateChannelDto) {

        channelService.updateChannel(channelLink, updateChannelDto);

        return new ResponseEntity("Channel successfully updated", OK);
    }

    @Operation(summary = "채널 상태 업데이트  - 준비중(PREPARING, 0), 진행중(PROCEEDING, 1), 끝남(FINISH, 2)")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "status", description = "채널 진행 상태 변경 쿼리 파라미터", example = "준비중(0), 진행중(1), 끝남(2)")
    }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PutMapping("/channel/{channelLink}")
    public ResponseEntity updateChannelStatus(@PathVariable("channelLink") String channelLink,
                                              @RequestParam("status") Integer status) {
        channelService.updateChannelStatus(channelLink, status);
        return new ResponseEntity("Channel Status Successfully updated", OK);
    }

    @Operation(summary = "채널 삭제 - 준비 중 상태의 채널만 삭제 가능")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
    }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelNotFoundException.class))),
            @ApiResponse(responseCode = "400", description = "채널 경기가 준비중이 아님", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelStatusAlreadyException.class))),
            @ApiResponse(responseCode = "401", description = "해당 채널의 호스트가 아님", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantNotGameHostException.class))),
            @ApiResponse(responseCode = "401", description = "해당 채널의 참가자가 아님", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidParticipantAuthException.class)))
    })
    @DeleteMapping("/channel/{channelLink}")
    public ResponseEntity deleteChannel(@PathVariable("channelLink") String channelLink) {

        channelDeleteService.deleteChannel(channelLink);

        return new ResponseEntity(OK);
    }
}
