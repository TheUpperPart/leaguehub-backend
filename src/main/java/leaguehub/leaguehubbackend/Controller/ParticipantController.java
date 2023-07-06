package leaguehub.leaguehubbackend.Controller;

import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseUserDetailDto getUserDetail(@RequestParam(value = "gameid") String nickname,
                                               @RequestParam(value = "gamecategory") Integer category){

        return participantService.selectGameCategory(nickname, category);
    }

    /**
     * api 주소 확정 필요
     * 역할이 관전자일 경우 참가 버튼을 누르면 model창 출력
     * @param channelId
     * @return responseEntity
     */
    @GetMapping("/participant/{channelId}")
    public ResponseEntity participateMatch(@PathVariable("channelId") Long channelId){

        participantService.checkParticipateMatch(channelId);

        return new ResponseEntity<>("Valid OBSERVER ROLE", HttpStatus.OK);
    }

    /**
     * api 주소 확정 필요
     * 관전자인 사용자가 해당 채널의 경기에 참가
     * @param responseDto
     * @return responseEntity
     */
    @PostMapping("/participant/match")
    public ResponseEntity participateMatch(@RequestBody ParticipantResponseDto responseDto){

        participantService.participateMatch(responseDto);

        return new ResponseEntity<>("Update Participant ROLE", HttpStatus.OK);
    }
}
