package leaguehub.leaguehubbackend.controller.channel;

import leaguehub.leaguehubbackend.dto.channel.ResponseBoardDetail;
import leaguehub.leaguehubbackend.service.channel.ChannelBoardService;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelBoardController {

    private final ChannelService channelService;
    private final ChannelBoardService channelBoardService;
    private final ParticipantService participantService;


    @GetMapping("/contents")
    public ResponseEntity loadBoardDetail(@RequestParam("channelLink") String channelLink,
                                          @RequestParam("boardId") Long boardId) {
        ResponseBoardDetail responseBoardDetail = channelBoardService.loadBoardDetail(channelLink, boardId);

        return new ResponseEntity(responseBoardDetail, OK);
    }
}
