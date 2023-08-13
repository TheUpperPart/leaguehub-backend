package leaguehub.leaguehubbackend.controller.channel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.dto.channel.*;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.channel.ChannelBoardService;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final ChannelBoardService channelBoardService;
    private final ParticipantService participantService;

    @Operation(summary = "채널 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseCreateChannelDto.class))),
            @ApiResponse(responseCode = "400", description = "Dto 유효성 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel")
    public ResponseEntity createChannel(@Valid @RequestBody CreateChannelDto createChannelDto
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                log.error(error.getObjectName());
            }

            return new ResponseEntity<>(errors, BAD_REQUEST);
        }

        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);

        return new ResponseEntity<>(responseCreateChannelDto, OK);
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
                .game(channelInfo.getCategory().name())
                .hostName(participantService.findChannelHost(channelLink))
                .participateNum(channelInfo.getRealPlayer())
                .maxPlayer(channelInfo.getMaxPlayer())
                .leagueTitle(channelInfo.getTitle())
                .permission(participantService.findParticipantPermission(channelLink))
                .build();

        return new ResponseEntity(responseChannelDto, OK);
    }

    @Operation(summary = "채널 가져오기 - 여러 채널(로그인시 사이드바 화면 구성)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dto를 리스트로 반환",content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantChannelDto.class))),
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

        return new ResponseEntity("Channel successfully updated",OK);
    }
}
