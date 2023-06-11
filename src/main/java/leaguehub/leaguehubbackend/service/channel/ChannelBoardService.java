package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.UpdateChannelBoardDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChannelBoardService {

    private final ChannelService channelService;
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void createChannelBoard(CreateChannelBoardDto createChannelBoardDto) {
        String personalId = UserUtil.getUserPersonalId();
        Member member = memberService.validateMember(personalId);
        Channel channel = channelService.validateChannel(createChannelBoardDto.getChannelId());

        Participant participant = participantRepository.findParticipantByMemberId(member.getId());

        if (participant.getRole() == Role.HOST && participant.getChannel() == channel) {
            channelBoardRepository.
                    save(ChannelBoard.createChannelBoard(channel, createChannelBoardDto));
        }
    }

    @Transactional
    public List<ChannelBoard> readChannelBoard(ChannelBoardDto readChannelBoardDto) {
        Channel channel = channelService.validateChannel(readChannelBoardDto.getChannelId());

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel);

        return channelBoards;
    }

    @Transactional
    public void updateChannelBoard(UpdateChannelBoardDto updateChannelBoardDto) {
        String personalId = UserUtil.getUserPersonalId();
        Member member = memberService.validateMember(personalId);
        Channel channel = channelService.validateChannel(updateChannelBoardDto.getChannelId());
        ChannelBoard channelBoard = validateChannelBoard(updateChannelBoardDto.getChannelBoardId());

        validateChannelAndChannelBoard(channel, channelBoard);

        Participant participant = participantRepository.findParticipantByMemberId(member.getId());

        if (participant.getRole() == Role.HOST && participant.getChannel() == channel) {
            channelBoardRepository.
                    save(channelBoard.updateChannelBoard(updateChannelBoardDto));
        }

    }

    @Transactional
    public void deleteChannelBoard(ChannelBoardDto deleteChannelBoardDto) {
        String personalId = UserUtil.getUserPersonalId();
        Member member = memberService.validateMember(personalId);
        Channel channel = channelService.validateChannel(deleteChannelBoardDto.getChannelId());
        ChannelBoard channelBoard = validateChannelBoard(deleteChannelBoardDto.getChannelBoardId());

        validateChannelAndChannelBoard(channel, channelBoard);

        Participant participant = participantRepository.findParticipantByMemberId(member.getId());

        if (participant.getRole() == Role.HOST && participant.getChannel() == channel) {
            channelBoardRepository.deleteById(channelBoard.getId());
        }
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
