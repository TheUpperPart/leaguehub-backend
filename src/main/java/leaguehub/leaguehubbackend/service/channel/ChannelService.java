package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.ParticipantChannelDto;
import leaguehub.leaguehubbackend.dto.channel.UpdateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.exception.email.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.match.MatchService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static leaguehub.leaguehubbackend.entity.member.BaseRole.USER;


@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;
    private final ParticipantRepository participantRepository;
    private final MatchService matchService;

    @Transactional
    public ParticipantChannelDto createChannel(CreateChannelDto createChannelDto) {

        Member member = memberService.findCurrentMember();

        checkEmail(SecurityUtils.getAuthenticatedUser());

        validateChannelRule(createChannelDto);

        Channel channel = Channel.createChannel(createChannelDto.getTitle(),
                createChannelDto.getGameCategory(), createChannelDto.getMaxPlayer(),
                createChannelDto.getMatchFormat(), createChannelDto.getChannelImageUrl(),
                createChannelDto.getTier(), createChannelDto.getTierMax(),
                createChannelDto.getTierMin(),
                createChannelDto.getPlayCount(),
                createChannelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));

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

        Channel findChannel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);

        ChannelDto channelDto = ChannelDto.builder().title(findChannel.getTitle())
                .realPlayer(findChannel.getRealPlayer()).gameCategory(findChannel.getGameCategory())
                .maxPlayer(findChannel.getMaxPlayer()).build();

        return channelDto;
    }

    @Transactional
    public void updateChannel(String channelLink, UpdateChannelDto updateChannelDto) {
        Member member = memberService.findCurrentMember();
        Channel channel = validateChannel(channelLink);
        Participant participant = getParticipant(channel.getId(), member.getId());
        checkRoleHost(participant.getRole());


        Optional.ofNullable(updateChannelDto.getTitle()).ifPresent(channel::updateTitle);
        Optional.ofNullable(updateChannelDto.getMaxPlayer()).ifPresent(channel::updateMaxPlayer);
        Optional.ofNullable(updateChannelDto.getChannelImageUrl()).ifPresent(channel::updateChannelImageUrl);
    }

    public Channel validateChannel(String channelLink) {
        Channel channel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);
        return channel;
    }


    public Participant getParticipant(Long channelId, Long memberId) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_Id(memberId, channelId)
                .orElseThrow(() -> new InvalidParticipantAuthException());
        return participant;
    }

    public void checkRoleHost(Role role) {
        if (role != Role.HOST) {
            throw new InvalidParticipantAuthException();
        }
    }

    private void validateTier(Integer tierMax, Integer tierMin) {
        if (tierMax == null && tierMin == null) {
            throw new ChannelRequestException();
        }
    }

    private void validatePlayCount(Integer playCountMin) {
        if (playCountMin == null) {
            throw new ChannelRequestException();
        }
    }

    private void validateChannelRule(CreateChannelDto createChannelDto) {
        if (createChannelDto.getTier()) {
            validateTier(createChannelDto.getTierMax(), createChannelDto.getTierMin());
        }

        if (createChannelDto.getPlayCount()) {
            validatePlayCount(createChannelDto.getPlayCountMin());
        }
    }

    private void checkEmail(UserDetails userDetails) {
        if (!userDetails.getAuthorities().toString().equals(USER.convertBaseRole()))
            throw new UnauthorizedEmailException();
    }

    private ParticipantChannelDto convertParticipantChannelDto(Participant participant) {
        Channel channel = participant.getChannel();
        return new ParticipantChannelDto(
                channel.getChannelLink(),
                channel.getTitle(),
                channel.getGameCategory().getNum(),
                channel.getChannelImageUrl(),
                participant.getIndex()
        );
    }

}
