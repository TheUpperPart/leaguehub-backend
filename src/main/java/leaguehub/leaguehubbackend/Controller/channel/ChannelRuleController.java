package leaguehub.leaguehubbackend.controller.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelRuleDto;
import leaguehub.leaguehubbackend.service.channel.ChannelRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelRuleController {

    private final ChannelRuleService channelRuleService;

    @GetMapping("/channel/{channelLink}/rule")
    public ResponseEntity getChannelRule(@PathVariable("channelLink") String channelLink) {
        ChannelRuleDto channelRule = channelRuleService.getChannelRule(channelLink);

        return new ResponseEntity<>(channelRule, OK);
    }

    @PostMapping("/channel/{channelLink}/rule")
    public ResponseEntity updateChannelRule(@PathVariable("channelLink") String channelLink,
                                            @RequestBody ChannelRuleDto channelRuleDto) {
        ChannelRuleDto channelRule = channelRuleService.updateChannelRule(channelLink, channelRuleDto);

        return new ResponseEntity(channelRule, OK);
    }
}
