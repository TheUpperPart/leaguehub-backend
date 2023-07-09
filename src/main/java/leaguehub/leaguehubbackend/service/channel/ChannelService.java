package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    public void createChannel(CreateChannelDto createChannelDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        Channel channel = Channel.createChannel(createChannelDto.getTitle(),
                createChannelDto.getGame(), createChannelDto.getParticipationNum(),
                createChannelDto.getTournament(), createChannelDto.getChannelImageUrl(),
                createChannelDto.getTier(), createChannelDto.getTierMax(),
                createChannelDto.getPlayCount(),
                createChannelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
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


}
