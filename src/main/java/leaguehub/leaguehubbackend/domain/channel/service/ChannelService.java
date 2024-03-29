package leaguehub.leaguehubbackend.domain.channel.service;

import leaguehub.leaguehubbackend.domain.channel.dto.ChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.UpdateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.*;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelStatusAlreadyException;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelBoardRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelInfoRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.email.exception.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.domain.match.service.MatchService;
import leaguehub.leaguehubbackend.domain.match.service.chat.MatchChatService;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.entity.Role;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static leaguehub.leaguehubbackend.domain.channel.entity.ChannelStatus.PROCEEDING;
import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.USER;


@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;
    private final ParticipantRepository participantRepository;
    private final MatchService matchService;
    private final ChannelRuleRepository channelRuleRepository;
    private final MatchChatService matchChatService;
    private final ChannelInfoRepository channelInfoRepository;

    @Transactional
    public ParticipantChannelDto createChannel(CreateChannelDto createChannelDto) {

        Member member = memberService.findCurrentMember();

        checkEmail(SecurityUtils.getAuthenticatedUser());


        Channel channel = Channel.createChannel(createChannelDto.getTitle(),
                createChannelDto.getGameCategory(), createChannelDto.getMaxPlayer(),
                createChannelDto.getMatchFormat(), createChannelDto.getChannelImageUrl());

        channelRepository.save(channel);

        ChannelRule channelRule = ChannelRule.createChannelRule(channel, createChannelDto.getTier(), createChannelDto.getTierMax(),
                createChannelDto.getTierMin(),
                createChannelDto.getPlayCount(),
                createChannelDto.getPlayCountMin());

        channelRuleRepository.save(channelRule);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        channelInfoRepository.save(ChannelInfo.createChannelInfo(channel));

        Participant participant = Participant.createHostChannel(member, channel);
        participant.newCustomChannelIndex(participantRepository.findMaxIndexByParticipant(member.getId()));

        participantRepository.save(participant);
        ParticipantChannelDto participantChannelDto = convertParticipantChannelDto(participant);

        matchService.createSubMatches(channel, createChannelDto.getMaxPlayer());

        return participantChannelDto;
    }

    @Transactional
    public List<ParticipantChannelDto> findParticipantChannelList() {

        Member member = memberService.findCurrentMember();


        List<Participant> allByParticipantList = participantRepository
                .findAllByMemberIdOrderByIndex(member.getId());

        List<ParticipantChannelDto> participantChannelDtoList = allByParticipantList.stream()
                .map(participant -> convertParticipantChannelDto(participant))
                .collect(Collectors.toList());

        return participantChannelDtoList;

    }

    @Transactional
    public ChannelDto findChannel(String channelLink) {

        Channel findChannel = getChannel(channelLink);

        ChannelDto channelDto = ChannelDto.builder().title(findChannel.getTitle())
                .realPlayer(findChannel.getRealPlayer()).gameCategory(findChannel.getGameCategory())
                .maxPlayer(findChannel.getMaxPlayer()).build();

        return channelDto;
    }

    @Transactional
    public void updateChannel(String channelLink, UpdateChannelDto updateChannelDto) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);
        Channel channel = participant.getChannel();
        checkRoleHost(participant.getRole());


        Optional.ofNullable(updateChannelDto.getTitle()).ifPresent(channel::updateTitle);
        Optional.ofNullable(updateChannelDto.getMaxPlayer()).ifPresent(channel::updateMaxPlayer);
        Optional.ofNullable(updateChannelDto.getChannelImageUrl()).ifPresent(channel::updateChannelImageUrl);
    }


    public Channel getChannel(String channelLink) {
        Channel channel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);
        return channel;
    }


    public Participant getParticipant(Long memberId, String channelLink) {
        return participantRepository
                .findParticipantByMemberIdAndChannel_ChannelLink(memberId, channelLink)
                .orElseThrow(() -> new InvalidParticipantAuthException());
    }

    public void checkRoleHost(Role role) {
        if (role != Role.HOST) {
            throw new InvalidParticipantAuthException();
        }
    }

    private void checkEmail(UserDetails userDetails) {
        if (!userDetails.getAuthorities().toString().equals(USER.convertBaseRole()))
            throw new UnauthorizedEmailException();
    }

    private ParticipantChannelDto convertParticipantChannelDto(Participant participant) {
        Channel channel = participant.getChannel();
        return new ParticipantChannelDto(
                channel.getId(),
                channel.getChannelLink(),
                channel.getTitle(),
                channel.getGameCategory().getNum(),
                channel.getChannelImageUrl(),
                participant.getIndex()
        );
    }

    public void updateChannelStatus(String channelLink, Integer status) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);
        checkRoleHost(participant.getRole());

        Channel channel = participant.getChannel();

        if(channel.getChannelStatus().equals(PROCEEDING))
            throw new ChannelStatusAlreadyException();

        channel.updateChannelStatus(ChannelStatus.convertStatus(status));

        if (status == 2) {
            matchChatService.deleteChannelMatchChat(channel);
        }

        if (status == 1) {
            matchService.processMatchSet(channelLink);
        }
    }
}
