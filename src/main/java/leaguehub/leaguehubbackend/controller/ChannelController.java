package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/api/channel")
    public ResponseEntity createChannel(@RequestBody CreateChannelDto createChannelDto) {
        String personalId = "";
        channelService.createChannel(createChannelDto, personalId);

        return new ResponseEntity<>("League successfully created", HttpStatus.OK);
    }
}
