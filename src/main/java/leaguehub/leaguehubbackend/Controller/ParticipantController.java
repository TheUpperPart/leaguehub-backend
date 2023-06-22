package leaguehub.leaguehubbackend.Controller;

import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantController {

    ParticipantService participantService;

    @GetMapping("/channel/getTier/{nickname}")
    public String getTier(@PathVariable("nickname") String nickname){

        return participantService.getTier(nickname);
    }
}
