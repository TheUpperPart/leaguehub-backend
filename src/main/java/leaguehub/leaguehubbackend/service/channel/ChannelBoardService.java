package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.RequestCreateChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.UpdateChannelBoardDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
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
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void createChannelBoard(Long channelId, RequestCreateChannelBoardDto request) {

    }

    /**
     * 채널 로딩 시점에서 불러오는 채널 게시판(내용은 반환하지 않음.)
     *
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
    public void updateChannelBoard(UpdateChannelBoardDto update) {

    }

    @Transactional
    public void deleteChannelBoard(ChannelBoardDto channelBoardDto, Long channelId) {

    }

    public ChannelBoard validateChannelBoard(Long channelBoardId) {
        return channelBoardRepository.findById(channelBoardId)
                .orElseThrow(ChannelBoardNotFoundException::new);
    }

    private void validateChannelAndChannelBoard(Channel channel, ChannelBoard channelBoard) {
        if (channel != channelBoard.getChannel()) {
            throw new ChannelBoardNotFoundException();
        }
    }


}
