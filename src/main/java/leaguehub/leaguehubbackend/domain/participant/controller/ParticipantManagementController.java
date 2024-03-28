package leaguehub.leaguehubbackend.domain.participant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantIdDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantIdResponseDto;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantManagementService;
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

@Tag(name = "Participants-Management-Controller", description = "참가자 관리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantManagementController {


    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ParticipantManagementService participantManagementService;


    @Operation(summary = "경기에 참가요청(TFT 만)", description = "관전자가 게임에 참가 요청")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가 요청 확인"),
            @ApiResponse(responseCode = "400", description = "해당 게임에 참여할 수 없는 상태입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/{channelLink}/participant")
    public ResponseEntity participateMatch(@PathVariable("channelLink") String channelLink, @RequestBody @Valid ParticipantDto responseDto){

        participantManagementService.participateMatch(responseDto, channelLink);

        return new ResponseEntity<>("Update Participant ROLE", OK);
    }

    @Operation(summary = "채널 참가", description = "채널 링크를 통하여 해당 채널 참가")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그 채널의 정보 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantChannelDto.class))),
            @ApiResponse(responseCode = "403", description = "해당 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/{channelLink}/participant/observer")
    public ResponseEntity enterChannel(@PathVariable("channelLink") String channelLink){

        ParticipantChannelDto participantChannelDto = participantManagementService.participateChannel(channelLink);

        return new ResponseEntity<>(participantChannelDto, OK);
    }

    @Operation(summary = "채널 나가기", description = "채널 링크를 통하여 해당 채널 나가기")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채널을 나갔습니다."),
            @ApiResponse(responseCode = "403", description = "해당 채널을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @DeleteMapping("/{channelLink}")
    public ResponseEntity leaveChannel(@PathVariable("channelLink") String channelLink){

        participantManagementService.leaveChannel(channelLink);

        return new ResponseEntity<>("Leave this Channel", OK);
    }

    @Operation(summary = "채널 순서를 커스텀하게 구성 - 로그인시 사이드바 화면 구성을 커스텀할 수 있음",
            description = "입력과 반환 Dto가 동일하게 되어서 API요청 필요 없이 바로 회면 구성 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dto를 리스트로 반환",content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantChannelDto.class))),
    })
    @PostMapping("/channels/order")
    public ResponseEntity updateCustomChannelIndex(@RequestBody List<ParticipantChannelDto> participantChannelDtoList){

        List<ParticipantChannelDto> updateCustomChannelIndexList = participantManagementService.updateCustomChannelIndex(participantChannelDtoList);

        return new ResponseEntity<>(updateCustomChannelIndexList, OK);
    }

    //실격, 기권에 대한 웹소켓
    @MessageMapping("/{channelLink}/{matchIdStr}/disqualification")
    public void disqualifiedParticipant(@DestinationVariable("channelLink") String channelLink,
                                        @DestinationVariable("matchIdStr") String matchIdStr,
                                        @Payload ParticipantIdDto message) {
        ParticipantIdResponseDto participantIdResponseDto = participantManagementService.disqualifiedParticipant(channelLink, message);

        simpMessagingTemplate.convertAndSend("/match/" + matchIdStr, participantIdResponseDto);
    }

}
