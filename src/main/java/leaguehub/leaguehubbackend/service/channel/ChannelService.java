package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.ParticipantChannelDto;
import leaguehub.leaguehubbackend.dto.channel.ResponseCreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;
    private final ParticipantRepository participantRepository;

    /**
     * @param createChannelDto
     * @return
     */
    @Transactional
    public ResponseCreateChannelDto createChannel(CreateChannelDto createChannelDto) {

        Member member = getMember();

        Channel channel = Channel.createChannel(createChannelDto.getTitle(),
                createChannelDto.getGame(), createChannelDto.getParticipationNum(),
                createChannelDto.getTournament(), createChannelDto.getChannelImageUrl(),
                createChannelDto.getTier(), createChannelDto.getTierMax(), createChannelDto.getGradeMax(),
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

        List<Participant> allByMemberId = participantRepository.findAllByMemberId(member.getId());
        List<ParticipantChannelDto> participantChannelDtoList = new ArrayList<>();
        for (Participant participant : allByMemberId) {
            Channel channel = participant.getChannel();
            participantChannelDtoList.add(new ParticipantChannelDto(channel.getChannelLink(), channel.getTitle(),
                    channel.getCategory().getNum(), channel.getChannelImageUrl()));
        }

        return participantChannelDtoList;
    }

    @Transactional
    public ChannelDto findChannel(String channelLink) {

        Channel findChannel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);

        ChannelDto channelDto = ChannelDto.builder().title(findChannel.getTitle())
                .realPlayer(findChannel.getRealPlayer()).category(findChannel.getCategory()).build();

        return channelDto;
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


}
