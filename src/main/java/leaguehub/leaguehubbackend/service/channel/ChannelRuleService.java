package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelRuleDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelRuleService {

    private final ChannelRuleRepository channelRuleRepository;
    private final ChannelService channelService;
    private final MemberService memberService;

    @Transactional
    public ChannelRuleDto updateChannelRule(String channelLink, ChannelRuleDto channelRuleDto) {
        Channel channel = channelService.validateChannel(channelLink);
        Member member = memberService.findCurrentMember();
        Participant participant = channelService.getParticipant(channel.getId(), member.getId());
        channelService.checkRoleHost(participant.getRole());

        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(channelLink);

        Optional.ofNullable(channelRuleDto.getTier())
                .ifPresent(tier -> {
                    if (tier) {
                        validateTier(channelRuleDto.getTierMax(), channelRuleDto.getTierMin());
                        channelRule.updateTierRule(true, channelRuleDto.getTierMax(), channelRuleDto.getTierMin());
                    } else {
                        channelRule.updateTierRule(false);
                    }
                });

        Optional.ofNullable(channelRuleDto.getPlayCount())
                .ifPresent(playCount -> {
                    if (playCount) {
                        validatePlayCount(channelRuleDto.getPlayCountMin());
                        channelRule.updatePlayCountMin(true, channelRuleDto.getPlayCountMin());
                    } else {
                        channelRule.updatePlayCountMin(false);
                    }
                });

        return new ChannelRuleDto().builder().tier(channelRule.getTier()).tierMax(channelRule.getTierMax())
                .tierMin(channelRule.getTierMin()).playCount(channelRule.getPlayCount())
                .playCountMin(channelRule.getLimitedPlayCount()).build();
    }

    @Transactional
    public ChannelRuleDto getChannelRule(String channelLink) {
        channelService.validateChannel(channelLink);
        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(channelLink);

        return new ChannelRuleDto().builder().tier(channelRule.getTier()).tierMax(channelRule.getTierMax())
                .tierMin(channelRule.getTierMin()).playCount(channelRule.getPlayCount())
                .playCountMin(channelRule.getLimitedPlayCount()).build();
    }


    private void validateTier(String tierMax, String tierMin) {
        if (tierMax == null && tierMin == null) {
            throw new ChannelRequestException();
        }
    }

    private void validatePlayCount(Integer playCountMin) {
        if (playCountMin == null) {
            throw new ChannelRequestException();
        }
    }
}
