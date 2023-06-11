package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.*;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberService memberService;

    /**
     * @param createChannelDto
     * @return
     */
    @Transactional
    public Long createChannel(CreateChannelDto createChannelDto) {

        String personalId = UserUtil.getUserPersonalId();

        Member member = memberService.validateMember(personalId);

        Channel channel = Channel.createChannel(createChannelDto, member);
        channelRepository.save(channel);
        Channel.createParticipationLink(channel);

        return channel.getId();
    }

    public Channel validateChannel(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(ChannelNotFoundException::new);
        return channel;
    }

}
