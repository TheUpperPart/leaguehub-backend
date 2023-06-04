package leaguehub.leaguehubbackend.service;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.member.MemberNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.*;
import leaguehub.leaguehubbackend.repository.member.*;
import leaguehub.leaguehubbackend.repository.particiapnt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final ChannelBoardRepository channelBoardRepository;
    private final ChannelRuleRepository channelRuleRepository;

    /**
     *
     * @param createChannelDto
     * @param personalId
     * @return channel.Id
     */
    @Transactional
    public Long createChannel(CreateChannelDto createChannelDto, String personalId) {

        Member member = memberRepository.findMemberByPersonalId(personalId)
                .orElseThrow(MemberNotFoundException::new);

        Channel channel = Channel.createChannel(createChannelDto, member);

        channelRepository.save(channel);

        return channel.getId();
    }
}
