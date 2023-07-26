package leaguehub.leaguehubbackend.controller.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardLoadDto;
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

    private final ChannelService channelService;
    private final ChannelBoardService channelBoardService;
    private final ParticipantService participantService;

    @PostMapping("/channel/{channelLink}/new")
    public ResponseEntity createChannelBoard(@PathVariable("channelLink") String channelLink,
                                             @RequestBody ChannelBoardDto request) {
        channelBoardService.createChannelBoard(channelLink, request);

        return new ResponseEntity("Board successfully created", OK);
    }

    @PostMapping("/channel/{channelLink}/{boardId}")
    public ResponseEntity updateChannelBoard(@PathVariable("channelLink") String channelLink,
                                          @PathVariable("boardId") Long boardId,
                                             @RequestBody ChannelBoardDto channelBoardDto) {
        channelBoardService.updateChannelBoard(channelLink, boardId, channelBoardDto);

        return new ResponseEntity("Board successfully updated", OK);
    }

    @DeleteMapping("/channel/{channelLink}/{boardId}")
    public ResponseEntity deleteChannelBoard(@PathVariable("channelLink") String channelLink,
                                             @PathVariable("boardId") Long boardId) {

        channelBoardService.deleteChannelBoard(channelLink, boardId);

        return new ResponseEntity("Board successfully deleted", OK);
    }

    @GetMapping("/channel/{channelLink}/{boardId}")
    public ResponseEntity getChannelBoard(@PathVariable("channelLink") String channelLink,
                                          @PathVariable("boardId") Long boardId) {
        ChannelBoardDto channelBoardDto = channelBoardService.getChannelBoard(channelLink, boardId);

        return new ResponseEntity(channelBoardDto, OK);
    }

    @PostMapping("/channel/{channelLink}/index")
    public ResponseEntity updateChannelBoardIndex(@PathVariable("channelLink") String channelLink,
                                                  @RequestBody List<ChannelBoardLoadDto> channelBoardLoadDtoList) {
        channelBoardService.updateChannelBoardIndex(channelLink, channelBoardLoadDtoList);

        return new ResponseEntity("BoardIndex successfully updated", OK);
    }
}
