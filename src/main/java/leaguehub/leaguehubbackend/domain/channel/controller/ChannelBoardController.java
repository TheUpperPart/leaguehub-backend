package leaguehub.leaguehubbackend.domain.channel.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardIndexListDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardInfoDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardLoadDto;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelBoardService;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelBoardController {

    private final ChannelBoardService channelBoardService;

    @Operation(summary = "채널 보드 만들기")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelBoardLoadDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음, 관리자 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}/new")
    public ResponseEntity createChannelBoard(@PathVariable("channelLink") String channelLink,
                                             @RequestBody @Valid ChannelBoardDto request,
                                             BindingResult bindingResult) {
        ChannelBoardLoadDto channelBoardLoadDto = channelBoardService.createChannelBoard(channelLink, request);

        return new ResponseEntity(channelBoardLoadDto, OK);
    }

    @Operation(summary = "채널 보드 업데이트")
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "boardId", description = "게시판 고유 Id", example = "0, 1, 2")
    })
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
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "boardId", description = "게시판 고유 Id", example = "0, 1, 2")
    })
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
    @Parameters(value = {
            @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88"),
            @Parameter(name = "boardId", description = "게시판 고유 Id", example = "0, 1, 2")
    })
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
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음, 권한x", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/channel/{channelLink}/order")
    public ResponseEntity updateChannelBoardIndex(@PathVariable("channelLink") String channelLink,
                                                  @RequestBody @Valid ChannelBoardIndexListDto channelBoardIndexListDto) {
        channelBoardService.updateChannelBoardIndex(channelLink, channelBoardIndexListDto.getChannelBoardLoadDtoList());

        return new ResponseEntity("BoardIndex successfully updated", OK);
    }

    @Operation(summary = "채널 보드 가져오기 - 채널 로드시 현재 라운드와 제목과 보드ID 가져오기 리스트로 반환")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChannelBoardInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "채널 링크가 올바르지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/channel/{channelLink}/boards")
    public ResponseEntity loadChannelBoards(@PathVariable("channelLink") String channelLink) {

        ChannelBoardInfoDto channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channelLink);

        return new ResponseEntity(channelBoardLoadDtoList, OK);
    }
}
