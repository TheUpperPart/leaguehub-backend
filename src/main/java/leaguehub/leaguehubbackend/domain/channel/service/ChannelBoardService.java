package leaguehub.leaguehubbackend.domain.channel.service;

import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardInfoDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardLoadDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelBoard;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelBoardRepository;
import leaguehub.leaguehubbackend.domain.match.dto.MyMatchDto;
import leaguehub.leaguehubbackend.domain.match.service.MatchService;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
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
    private final MatchService matchService;

    @Transactional
    public ChannelBoardLoadDto createChannelBoard(String channelLink, ChannelBoardDto request) {

        Member member = memberService.findCurrentMember();
        Participant participant = channelService.getParticipant(member.getId(), channelLink);
        Channel channel = participant.getChannel();
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
    public ChannelBoardInfoDto loadChannelBoards(String channelLink) {


        channelService.getChannel(channelLink);

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_ChannelLinkOrderByIndex(channelLink);

        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoards.stream()
                .map(channelBoard -> new ChannelBoardLoadDto(channelBoard.getId(), channelBoard.getTitle(), channelBoard.getIndex()))
                .collect(Collectors.toList());

        MyMatchDto matchDto = matchService.getMyMatchRound(channelLink);

        return new ChannelBoardInfoDto(matchDto.getMyMatchRound(), matchDto.getMyMatchId(), channelBoardLoadDtoList);
    }

    @Transactional
    public ChannelBoardDto getChannelBoard(String channelLink, Long boardId) {
        ChannelBoard channelBoard = validateChannelBoard(boardId, channelLink);

        return new ChannelBoardDto(channelBoard.getTitle(), channelBoard.getContent());
    }

    @Transactional
    public void updateChannelBoard(String channelLink, Long boardId, ChannelBoardDto update) {

        Member member = memberService.findCurrentMember();

        Participant participant = channelService.getParticipant(member.getId(), channelLink);

        Channel channel = participant.getChannel();
        channelService.checkRoleHost(participant.getRole());

        ChannelBoard channelBoard = validateChannelBoard(boardId, channel.getId());


        channelBoard.updateChannelBoard(update.getTitle(), update.getContent());
    }

    @Transactional
    public void deleteChannelBoard(String channelLink, Long boardId) {
        Member member = memberService.findCurrentMember();

        Participant participant = channelService.getParticipant(member.getId(), channelLink);
        Channel channel = participant.getChannel();

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

        Participant participant = channelService.getParticipant(member.getId(), channelLink);

        Channel channel = participant.getChannel();

        channelService.checkRoleHost(participant.getRole());

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());

        channelBoardLoadDtoList.forEach(channelBoardLoadDto -> {
            channelBoards.stream()
                    .filter(channelBoard -> channelBoard.getId().equals(channelBoardLoadDto.getBoardId()))
                    .findFirst()
                    .ifPresent(channelBoard -> channelBoard.updateIndex(channelBoardLoadDto.getBoardIndex()));
        });
    }

    public ChannelBoard validateChannelBoard(Long channelBoardId, String channelLink) {
        return channelBoardRepository.findChannelBoardsByIdAndChannel_ChannelLink(channelBoardId, channelLink)
                .orElseThrow(() -> new ChannelBoardNotFoundException());
    }

    public ChannelBoard validateChannelBoard(Long channelBoardId, Long channelId) {
        return channelBoardRepository.findChannelBoardsByIdAndChannel_Id(channelBoardId, channelId)
                .orElseThrow(() -> new ChannelBoardNotFoundException());
    }

}
