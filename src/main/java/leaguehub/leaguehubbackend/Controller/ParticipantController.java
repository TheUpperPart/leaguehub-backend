package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import leaguehub.leaguehubbackend.dto.participant.RequestPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * nickname과 category를 받아 게임 티어 검색
     * @param nickname
     * @param category
     * @return
     */
    @GetMapping("/stat")
    public ResponseEntity getUserDetail(@RequestParam(value = "gameid") String nickname,
                                               @RequestParam(value = "gamecategory") Integer category){

        ResponseUserDetailDto userDetailDto = participantService.selectGameCategory(nickname, category);

        return new ResponseEntity<>(userDetailDto, HttpStatus.OK);
    }

    /**
     * api 주소 확정 필요
     * 역할이 관전자일 경우 참가 버튼을 누르면 model창 출력
     * @param channelLink
     * @return responseEntity
     */
    @GetMapping("/participant/{channelLink}")
    public ResponseEntity participateMatch(@PathVariable("channelLink") String channelLink){

        participantService.checkParticipateMatch(channelLink);

        return new ResponseEntity<>("Valid OBSERVER ROLE", HttpStatus.OK);
    }

    /**
     * api 주소 확정 필요
     * 관전자인 사용자가 해당 채널의 경기에 참가(TFT 만)
     * @param responseDto
     * @return responseEntity
     */
    @PostMapping("/participant/match")
    public ResponseEntity participateMatch(@RequestBody ParticipantResponseDto responseDto){

        participantService.participateMatch(responseDto);

        return new ResponseEntity<>("Update Participant ROLE", HttpStatus.OK);
    }

    /**
     * 채널에 PLAYER 역할인 사람들을 반환
     * @param channelLink
     * @return ResponseEntity
     */
    @GetMapping("/player")
    public ResponseEntity loadPlayer(@RequestParam(value = "channelLink") String channelLink){

        List<RequestPlayerDto> players = participantService.loadPlayers(channelLink);

        return new ResponseEntity<>(players, HttpStatus.OK);
    }
}
