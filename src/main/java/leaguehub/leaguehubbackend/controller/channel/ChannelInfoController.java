package leaguehub.leaguehubbackend.controller.channel;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.dto.channel.ChannelInfoDto;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.channel.ChannelInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelInfoController {

    private final ChannelInfoService channelInfoService;

    @Operation(summary = "채널 상품, 참가조건, 경기시간 정보 수정하기")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}/main")
    public ResponseEntity updateChannelInfo(@PathVariable("channelLink") String channelLink,
                                            @RequestBody @Valid ChannelInfoDto channelInfoDto) {

        channelInfoService.updateChannelInfo(channelLink, channelInfoDto);

        return new ResponseEntity("수정이 완료되었습니다.",OK);
    }

    @Operation(summary = "채널 상품, 참가조건, 경기시간 정보 가져오기")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}/main")
    public ResponseEntity getChannelInfo(@PathVariable("channelLink") String channelLink) {

        ChannelInfoDto channelInfoDto = channelInfoService.getChannelInfoDto(channelLink);

        return new ResponseEntity(channelInfoDto, OK);
    }
}
