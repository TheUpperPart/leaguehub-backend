package leaguehub.leaguehubbackend.domain.participant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantRoleAndPermissionService;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Participants-RoleAndPermission-Controller", description = "참가자 역할 및 권한 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantRoleAndPermissionController {


    private final ParticipantRoleAndPermissionService participantRoleAndPermissionService;


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
    public ResponseEntity approveParticipantRequest(@PathVariable("channelLink") String channelLink,
                                                    @PathVariable("participantId") Long participantId){

        participantRoleAndPermissionService.approveParticipantRequest(channelLink, participantId);

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
    public ResponseEntity rejectParticipantRequest(@PathVariable("channelLink") String channelLink,
                                                   @PathVariable("participantId") Long participantId){

        participantRoleAndPermissionService.rejectedParticipantRequest(channelLink, participantId);

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
                                                @PathVariable("participantId") Long participantId){

        participantRoleAndPermissionService.updateHostRole(channelLink, participantId);

        return new ResponseEntity<>("update HOST", OK);
    }

    @Operation(summary = "관리자 권한 확인", description = "관리자인지 확인하는 기능")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin Check"),
            @ApiResponse(responseCode = "401", description = "해당 권한이 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}/permission")
    public ResponseEntity checkHost(@PathVariable("channelLink") String channelLink){

        participantRoleAndPermissionService.checkAdminHost(channelLink);

        return new ResponseEntity<>("Admin Check", OK);
    }
}
