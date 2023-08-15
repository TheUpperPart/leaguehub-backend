package leaguehub.leaguehubbackend.controller.channel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import leaguehub.leaguehubbackend.dto.channel.ChannelRuleDto;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.channel.ChannelRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelRuleController {

    private final ChannelRuleService channelRuleService;

    @Operation(summary = "채널 룰 가져오기)")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelRuleDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}/rule")
    public ResponseEntity getChannelRule(@PathVariable("channelLink") String channelLink) {
        ChannelRuleDto channelRule = channelRuleService.getChannelRule(channelLink);

        return new ResponseEntity<>(channelRule, OK);
    }

    @Operation(summary = "채널 룰 업데이트 - 티어, 판수)")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelRuleDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}/rule")
    public ResponseEntity updateChannelRule(@PathVariable("channelLink") String channelLink,
                                            @RequestBody ChannelRuleDto channelRuleDto) {
        ChannelRuleDto channelRule = channelRuleService.updateChannelRule(channelLink, channelRuleDto);

        return new ResponseEntity(channelRule, OK);
    }
}
