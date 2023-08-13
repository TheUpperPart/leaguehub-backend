package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardLoadDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public ChannelBoardLoadDto createChannelBoard(String channelLink, ChannelBoardDto request) {

        Member member = memberService.findCurrentMember();
        Channel channel = channelService.validateChannel(channelLink);
        Participant participant = channelService.getParticipant(channel.getId(), member.getId());
        channelService.checkRoleHost(participant.getRole());

        Integer maxIndexByChannel = channelBoardRepository.findMaxIndexByChannel(channel);


        ChannelBoard channelBoard = ChannelBoard.createChannelBoard(channel,
                request.getTitle(), request.getContent(), maxIndexByChannel + 1);
        channelBoardRepository.save(channelBoard);

        return new ChannelBoardLoadDto(channelBoard.getId(), channelBoard.getTitle(), channelBoard.getIndex());
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

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());

        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoards.stream()
                .map(channelBoard -> new ChannelBoardLoadDto(channelBoard.getId(), channelBoard.getTitle(), channelBoard.getIndex()))
                .collect(Collectors.toList());

        return channelBoardLoadDtoList;
    }

    @Transactional
    public ChannelBoardDto getChannelBoard(String channelLink, Long boardId) {
        Channel channel = channelService.validateChannel(channelLink);
        ChannelBoard channelBoard = validateChannelBoard(boardId, channel.getId());


        return new ChannelBoardDto(channelBoard.getTitle(), channelBoard.getContent());
    }

    @Transactional
    public void updateChannelBoard(String channelLink, Long boardId, ChannelBoardDto update) {

        Member member = memberService.findCurrentMember();

        Channel channel = channelService.validateChannel(channelLink);
        Participant participant = channelService.getParticipant(channel.getId(), member.getId());
        channelService.checkRoleHost(participant.getRole());

        ChannelBoard channelBoard = validateChannelBoard(boardId, channel.getId());


        channelBoard.updateChannelBoard(update.getTitle(), update.getContent());
    }

    @Transactional
    public void deleteChannelBoard(String channelLink, Long boardId) {
        Member member = memberService.findCurrentMember();

        Channel channel = channelService.validateChannel(channelLink);
        Participant participant = channelService.getParticipant(channel.getId(), member.getId());

        channelService.checkRoleHost(participant.getRole());

        ChannelBoard channelBoard = validateChannelBoard(boardId, channel.getId());


        channelBoardRepository.delete(channelBoard);
        List<ChannelBoard> boardsAfterDeleted = channelBoardRepository.findAllByChannelAndIndexGreaterThan(channel, channelBoard.getIndex());
        for (ChannelBoard board : boardsAfterDeleted) {
            board.updateIndex(board.getIndex() - 1);
        }
    }

    @Transactional
    public void updateChannelBoardIndex(String channelLink, List<ChannelBoardLoadDto> channelBoardLoadDtoList) {
        Member member = memberService.findCurrentMember();

        Channel channel = channelService.validateChannel(channelLink);
        Participant participant = channelService.getParticipant(channel.getId(), member.getId());

        channelService.checkRoleHost(participant.getRole());

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());

        channelBoardLoadDtoList.forEach(channelBoardLoadDto -> {
            channelBoards.stream()
                    .filter(channelBoard -> channelBoard.getId() == channelBoardLoadDto.getBoardId())
                    .findFirst()
                    .ifPresent(channelBoard -> channelBoard.updateIndex(channelBoardLoadDto.getBoardIndex()));
        });
    }

    public ChannelBoard validateChannelBoard(Long channelBoardId, Long channelId) {
        return channelBoardRepository.findChannelBoardsByIdAndChannel_Id(channelBoardId, channelId)
                .orElseThrow(() -> new ChannelBoardNotFoundException());
    }

}
