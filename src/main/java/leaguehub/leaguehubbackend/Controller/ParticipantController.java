package leaguehub.leaguehubbackend.Controller;

import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping("/stat")
    public ResponseUserDetailDto getUserDetail(@RequestParam(value = "gameid") String nickname,
                                               @RequestParam(value = "gamecategory") Integer category){

        return participantService.selectGameCategory(nickname, category);
    }
}
