package leaguehub.leaguehubbackend.controller.channel;

import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.ResponseChannelDto;
import leaguehub.leaguehubbackend.service.channel.ChannelBoardService;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final ChannelBoardService channelBoardService;
    private final ParticipantService participantService;

    @PostMapping("/channel")
    public ResponseEntity createChannel(@Valid @RequestBody CreateChannelDto createChannelDto
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                log.error(error.getObjectName());
            }

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        channelService.createChannel(createChannelDto);

        return new ResponseEntity<>("League successfully created", HttpStatus.OK);
    }

    @GetMapping("/channel/{channelLink}")
    public ResponseEntity getChannel(@PathVariable("channelLink") String channelLink) {

        ChannelDto channelInfo = channelService.findChannel(channelLink);
        List<ChannelBoardDto> channelBoards = channelBoardService.findChannelBoards(channelLink);

        ResponseChannelDto responseChannelDto = ResponseChannelDto.builder()
                .channelBoardDtoList(channelBoards)
                .game(channelInfo.getCategory().name())
                .hostName(participantService.findChannelHost(channelLink))
                .leagueTitle(channelInfo.getTitle())
                .permission(participantService.findParticipantPermission(channelLink))
                .build();

        return new ResponseEntity(responseChannelDto, HttpStatus.OK);
    }
}
