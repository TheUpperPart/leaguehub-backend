package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.dto.match.MatchRoundListDto;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.match.MatchRankService;
import leaguehub.leaguehubbackend.service.match.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MatchController {

    private final MatchRankService matchRankService;
    private final MatchService matchService;

    @Operation(summary = "매치 결과 조회 및 저장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치 결과가 생성되었습니다."),
            @ApiResponse(responseCode = "403", description = "매치 결과를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/match/matchResult")
    public ResponseEntity createMatchRank(@RequestBody MatchResponseDto matchResponseDto){

        matchRankService.setMatchRank(matchResponseDto);

        return new ResponseEntity<>("매치 결과가 생성되었습니다.", HttpStatus.CREATED);
    }

    @Operation(summary = "저장된 매치 결과 출력")
    @Parameter(name = "matchCode", description = "조회할 라이엇 매치코드", example = "KR_6519793792")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "매치 결과 리스트 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchRankResultDto.class))),
            @ApiResponse(responseCode = "403", description = "매치 결과를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/match/matchRank/{matchCode}")
    public ResponseEntity matchRankResult(@PathVariable("matchCode") String matchCode){

        List<MatchRankResultDto> resultDto = matchRankService.getMatchDetail(matchCode);

        return new ResponseEntity<>(resultDto, HttpStatus.OK);
    }

    @Operation(summary = "라운드 수(몇 강) 리스트 반환")
    @Parameter(name = "channelLink", description = "해당 채널의 링크", example = "42aa1b11ab88")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "라운드(몇 강) 리스트 반환", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MatchRoundListDto.class))),
            @ApiResponse(responseCode = "403", description = "매치 결과를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @GetMapping("/match/{channelLink}")
    public ResponseEntity loadMatchRoundList(@PathVariable("channelLink") String channelLink){

        MatchRoundListDto roundList = matchService.getRoundList(channelLink);

        return new ResponseEntity<>(roundList, HttpStatus.OK);
    }
}
