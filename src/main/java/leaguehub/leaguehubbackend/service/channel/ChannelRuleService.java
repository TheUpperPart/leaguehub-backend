package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelRuleDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
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
        Member member = memberService.findCurrentMember();
        Participant participant = channelService.getParticipant(member.getId(), channelLink);
        Channel channel = participant.getChannel();
        channelService.checkRoleHost(participant.getRole());

        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_Id(channel.getId());

        Optional.ofNullable(channelRuleDto.getTier())
                .ifPresent(tier -> {
                    if (tier) {
                        channelRule.updateTierRule(true, channelRuleDto.getTierMax(), channelRuleDto.getTierMin());
                    } else {
                        channelRule.updateTierRule(false);
                    }
                });

        Optional.ofNullable(channelRuleDto.getPlayCount())
                .ifPresent(playCount -> {
                    if (playCount) {
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
        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(channelLink);

        return new ChannelRuleDto().builder().tier(channelRule.getTier()).tierMax(channelRule.getTierMax())
                .tierMin(channelRule.getTierMin()).playCount(channelRule.getPlayCount())
                .playCountMin(channelRule.getLimitedPlayCount()).build();
    }

}
