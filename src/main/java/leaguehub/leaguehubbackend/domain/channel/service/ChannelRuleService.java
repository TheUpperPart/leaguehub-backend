package leaguehub.leaguehubbackend.domain.channel.service;

import leaguehub.leaguehubbackend.domain.channel.dto.ChannelRuleDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
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
