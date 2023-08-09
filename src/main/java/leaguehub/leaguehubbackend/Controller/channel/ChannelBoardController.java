package leaguehub.leaguehubbackend.controller.channel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardLoadDto;
import leaguehub.leaguehubbackend.dto.channel.ResponseChannelDto;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.channel.ChannelBoardService;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
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
public class ChannelBoardController {

    private final ChannelBoardService channelBoardService;

    @Operation(summary = "채널 보드 만들기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelBoardLoadDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음, 관리자 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}/new")
    public ResponseEntity createChannelBoard(@PathVariable("channelLink") String channelLink,
                                             @RequestBody ChannelBoardDto request) {
        ChannelBoardLoadDto channelBoardLoadDto = channelBoardService.createChannelBoard(channelLink, request);

        return new ResponseEntity(channelBoardLoadDto, OK);
    }

    @Operation(summary = "채널 보드 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음, 관리자 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}/{boardId}")
    public ResponseEntity updateChannelBoard(@PathVariable("channelLink") String channelLink,
                                          @PathVariable("boardId") Long boardId,
                                             @RequestBody ChannelBoardDto channelBoardDto) {
        channelBoardService.updateChannelBoard(channelLink, boardId, channelBoardDto);

        return new ResponseEntity("Board successfully updated", OK);
    }

    @Operation(summary = "채널 보드 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음, 관리자 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @DeleteMapping("/channel/{channelLink}/{boardId}")
    public ResponseEntity deleteChannelBoard(@PathVariable("channelLink") String channelLink,
                                             @PathVariable("boardId") Long boardId) {

        channelBoardService.deleteChannelBoard(channelLink, boardId);

        return new ResponseEntity("Board successfully deleted", OK);
    }

    @Operation(summary = "채널 보드 가져오기 - 단일 채널 보드 읽기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelBoardDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}/{boardId}")
    public ResponseEntity getChannelBoard(@PathVariable("channelLink") String channelLink,
                                          @PathVariable("boardId") Long boardId) {
        ChannelBoardDto channelBoardDto = channelBoardService.getChannelBoard(channelLink, boardId);

        return new ResponseEntity(channelBoardDto, OK);
    }

    @Operation(summary = "채널 보드 인덱스 업데이트 - 채널 보드 인덱스 커스텀")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음, 권한x", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}/index")
    public ResponseEntity updateChannelBoardIndex(@PathVariable("channelLink") String channelLink,
                                                  @RequestBody List<ChannelBoardLoadDto> channelBoardLoadDtoList) {
        channelBoardService.updateChannelBoardIndex(channelLink, channelBoardLoadDtoList);

        return new ResponseEntity("BoardIndex successfully updated", OK);
    }

    @Operation(summary = "채널 보드 가져오기 - 채널 로드시 제목과 보드ID 가져오기 리스트로 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelBoardLoadDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}/boards")
    public ResponseEntity loadChannelBoards(@PathVariable("channelLink") String channelLink) {

        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channelLink);

        return new ResponseEntity(channelBoardLoadDtoList, OK);
    }
}
