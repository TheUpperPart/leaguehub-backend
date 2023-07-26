package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardLoadDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChannelBoardService {

    private final ChannelService channelService;
    private final ChannelBoardRepository channelBoardRepository;
    private final MemberService memberService;
    private final ParticipantRepository participantRepository;

    @Transactional
    public ChannelBoardLoadDto createChannelBoard(String channelLink, ChannelBoardDto request) {

        Member member = getMember();
        Channel channel = channelService.validateChannel(channelLink);
        Participant participant = getParticipant(channelLink, member);

        if (participant.getRole() == Role.HOST) {
            ChannelBoard channelBoard = ChannelBoard.createChannelBoard(channel, request.getTitle(), request.getContent());
            channelBoardRepository.save(channelBoard);
            return new ChannelBoardLoadDto(channelBoard.getId(), channelBoard.getTitle());
        } else {
            throw new InvalidParticipantAuthException();
        }
    }


    /**
     * 채널 로딩 시점에서 불러오는 채널 게시판(내용은 반환하지 않음.)
     *
     * @param channelLink
     * @return List
     */
    @Transactional
    public List<ChannelBoardLoadDto> loadChannelBoards(String channelLink) {
        Channel channel = channelService.validateChannel(channelLink);

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel);

        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoards.stream()
                .map(channelBoard -> new ChannelBoardLoadDto(channelBoard.getId(), channelBoard.getTitle()))
                .collect(Collectors.toList());

        return channelBoardLoadDtoList;
    }

    @Transactional
    public ChannelBoardDto getChannelBoard(String channelLink, Long boardId) {
        Channel channel = channelService.validateChannel(channelLink);
        ChannelBoard channelBoard = validateChannelBoard(boardId);

        if (channelBoard.getChannel() != channel) {
            throw new ChannelBoardNotFoundException();
        }

        return new ChannelBoardDto(channelBoard.getTitle(), channelBoard.getContent());
    }

    @Transactional
    public void updateChannelBoard(String channelLink, Long boardId, ChannelBoardDto update) {

        Member member = getMember();

        channelService.validateChannel(channelLink);
        Participant participant = getParticipant(channelLink, member);

        ChannelBoard channelBoard = validateChannelBoard(boardId);

        if (participant.getRole() == Role.HOST) {
            channelBoard.updateChannelBoard(update.getTitle(), update.getContent());
        } else {
            throw new InvalidParticipantAuthException();
        }

    }

    @Transactional
    public void deleteChannelBoard(String channelLink, Long boardId) {
        Member member = getMember();

        channelService.validateChannel(channelLink);
        Participant participant = getParticipant(channelLink, member);

        ChannelBoard channelBoard = validateChannelBoard(boardId);

        if (participant.getRole() == Role.HOST) {
            channelBoardRepository.delete(channelBoard);
        } else {
            throw new InvalidParticipantAuthException();
        }

    }


    private Member getMember() {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);
        return member;
    }

    private Participant getParticipant(String channelLink, Member member) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink)
                .orElseThrow(() -> new InvalidParticipantAuthException());
        return participant;
    }

    public ChannelBoard validateChannelBoard(Long channelBoardId) {
        return channelBoardRepository.findById(channelBoardId)
                .orElseThrow(ChannelBoardNotFoundException::new);
    }


}
