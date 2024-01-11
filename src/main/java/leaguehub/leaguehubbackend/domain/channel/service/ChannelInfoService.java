package leaguehub.leaguehubbackend.domain.channel.service;


import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelInfoDto;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelInfo;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelInfoRepository;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelInfoService {

    private final ChannelInfoRepository channelInfoRepository;
    private final ChannelService channelService;
    private final MemberService memberService;


    public ChannelInfoDto getChannelInfoDto(String channelLink) {

        ChannelInfo channelInfo = validateChannelBoard(channelLink);
        String channelTitle = channelInfo.getChannel().getTitle();

        return new ChannelInfoDto(channelTitle, channelInfo.getChannelContentInfo(), channelInfo.getChannelRuleInfo(), channelInfo.getChannelTimeInfo(), channelInfo.getChannelPrizeInfo());
    }

    public void updateChannelInfo(String channelLink, ChannelInfoDto channelInfoDto) {
        Member member = memberService.findCurrentMember();
        Participant participant = channelService.getParticipant(member.getId(), channelLink);
        channelService.checkRoleHost(participant.getRole());

        ChannelInfo findChannelInfo = validateChannelBoard(channelLink);

        findChannelInfo.updateChannelBoard(channelInfoDto.getChannelContentInfo(), channelInfoDto.getChannelTimeInfo(), channelInfoDto.getChannelRuleInfo(), channelInfoDto.getChannelPrizeInfo());
    }


    public ChannelInfo validateChannelBoard(String channelLink) {
        return channelInfoRepository.findChannelInfoByChannel_ChannelLink(channelLink)
                .orElseThrow(() -> new ChannelNotFoundException());
    }
}
