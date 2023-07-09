package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.RequestCreateChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ResponseBoardDetail;
import leaguehub.leaguehubbackend.dto.channel.UpdateChannelBoardDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void createChannelBoard(String channelLink, RequestCreateChannelBoardDto request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        List<Participant> findByMemberId = participantRepository.findAllByMemberId(member.getId());
        boolean flag = false;
        for (Participant participant : findByMemberId) {
            if((participant.getRole() == Role.HOST || participant.getRole() == Role.MANAGER)
                    && participant.getChannel().getChannelLink() == channelLink) {
                ChannelBoard channelBoard = ChannelBoard.createChannelBoard(participant.getChannel()
                        , request.getTitle(), request.getContent());
                channelBoardRepository.save(channelBoard);
                flag = true;
            }
        }

        if(!flag) throw new InvalidParticipantAuthException();
    }

    /**
     * 채널 로딩 시점에서 불러오는 채널 게시판(내용은 반환하지 않음.)
     * @param channelId
     * @return List
     */
    @Transactional
    public List<ChannelBoardDto> findChannelBoards(String channelId) {
        Channel channel = channelService.validateChannel(channelId);

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel);

        List<ChannelBoardDto> channelBoardDtoList = channelBoards.stream()
                .map(channelBoard -> new ChannelBoardDto(channelBoard.getId(), channelBoard.getTitle()))
                .collect(Collectors.toList());

        return channelBoardDtoList;
    }

    @Transactional
    public ResponseBoardDetail loadBoardDetail(String channelLink, Long boardId) {
        Channel channel = channelService.validateChannel(channelLink);
        ChannelBoard channelBoard = validateChannelBoard(boardId);

        if(channelBoard.getChannel() != channel) {
            throw new ChannelBoardNotFoundException();
        }

        return new ResponseBoardDetail(channelBoard.getContent());
    }

    @Transactional
    public void updateChannelBoard(UpdateChannelBoardDto update) {

    }

    @Transactional
    public void deleteChannelBoard(String channelLink, Long boardId) {

    }

    public ChannelBoard validateChannelBoard(Long channelBoardId) {
        return channelBoardRepository.findById(channelBoardId)
                .orElseThrow(ChannelBoardNotFoundException::new);
    }


}
