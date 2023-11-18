package leaguehub.leaguehubbackend.service.channel;


import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.channel.ChannelInfoDto;
import leaguehub.leaguehubbackend.entity.channel.ChannelInfo;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelInfoRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
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
