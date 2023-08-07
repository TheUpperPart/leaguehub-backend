package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.*;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public ResponseCreateChannelDto createChannel(CreateChannelDto createChannelDto) {

        Member member = getMember();

        validateChannelRule(createChannelDto);

        Channel channel = Channel.createChannel(createChannelDto.getTitle(),
                createChannelDto.getGame(), createChannelDto.getParticipationNum(),
                createChannelDto.getTournament(), createChannelDto.getChannelImageUrl(),
                createChannelDto.getTier(), createChannelDto.getTierMax(),
                createChannelDto.getTierMin(),
                createChannelDto.getPlayCount(),
                createChannelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));

        return new ResponseCreateChannelDto(channel.getChannelLink());
    }

    @Transactional
    public List<ParticipantChannelDto> findParticipantChannelList() {
        Member member = getMember();

        List<Participant> allByParticipantList = participantRepository.findAllByMemberId(member.getId());

        List<ParticipantChannelDto> participantChannelDtoList = allByParticipantList.stream()
                .map(participant -> {
                    Channel channel = participant.getChannel();
                    return new ParticipantChannelDto(
                            channel.getChannelLink(),
                            channel.getTitle(),
                            channel.getCategory().getNum(),
                            channel.getChannelImageUrl()
                    );
                })
                .collect(Collectors.toList());

        return participantChannelDtoList;

    }

    @Transactional
    public ChannelDto findChannel(String channelLink) {

        Channel findChannel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);

        ChannelDto channelDto = ChannelDto.builder().title(findChannel.getTitle())
                .realPlayer(findChannel.getRealPlayer()).category(findChannel.getCategory())
                .maxPlayer(findChannel.getMaxPlayer()).build();

        return channelDto;
    }

    @Transactional
    public void updateChannel(String channelLink, UpdateChannelDto updateChannelDto) {
        Member member = getMember();
        Channel channel = validateChannel(channelLink);
        Participant participant = getParticipant(channel.getId(), member.getId());
        checkAuth(participant.getRole());


        Optional.ofNullable(updateChannelDto.getTitle()).ifPresent(channel::updateTitle);
        Optional.ofNullable(updateChannelDto.getParticipationNum()).ifPresent(channel::updateMaxPlayer);
        Optional.ofNullable(updateChannelDto.getChannelImageUrl()).ifPresent(channel::updateChannelImageUrl);
    }

    public Channel validateChannel(String channelLink) {
        Channel channel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);
        return channel;
    }

    private Member getMember() {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        String personalId = userDetails.getUsername();

        return memberService.validateMember(personalId);
    }

    private Participant getParticipant(Long channelId, Long memberId) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_Id(memberId, channelId)
                .orElseThrow(() -> new InvalidParticipantAuthException());
        return participant;
    }

    private void checkAuth(Role role) {
        if (role != Role.HOST) {
            throw new InvalidParticipantAuthException();
        }
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

    private void validateChannelRule(CreateChannelDto createChannelDto) {
        if (createChannelDto.getTier()) {
            validateTier(createChannelDto.getTierMax(), createChannelDto.getTierMin());
        }

        if (createChannelDto.getPlayCount()) {
            validatePlayCount(createChannelDto.getPlayCountMin());
        }
    }

}
