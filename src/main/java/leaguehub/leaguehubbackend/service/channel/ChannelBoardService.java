package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.UpdateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;

import leaguehub.leaguehubbackend.exception.channel.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ChannelBoardService {

    private final ChannelService channelService;
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;

    @Transactional
    public Optional<ChannelBoard> createChannelBoard(CreateChannelBoardDto createChannelBoardDto) {
        String personalId = UserUtil.getUserPersonalId();
        Member member = memberService.validateMember(personalId);
        Channel channel = channelService.validateChannel(createChannelBoardDto.getChannelId());

        List<Participant> participantList = member.getParticipant();

        Optional<ChannelBoard> channelBoard = participantList.stream()
                .filter(participant -> participant.getRole() == Role.HOST && participant.getChannel().getId() == channel.getId())
                .map(participant -> ChannelBoard.createChannelBoard(channel, createChannelBoardDto))
                .findFirst();

        return channelBoard;
    }

    @Transactional
    public ChannelBoard readChannelBoard(ChannelBoardDto readChannelBoardDto) {
        Channel channel = channelService.validateChannel(readChannelBoardDto.getChannelId());
        ChannelBoard channelBoard = validateChannelBoard(readChannelBoardDto.getChannelBoardId());

        validateChannelAndChannelBoard(channel, channelBoard);

        return channelBoard;
    }

    @Transactional
    public Optional<ChannelBoard> updateChannelBoard(UpdateChannelDto updateChannelDto) {
        String personalId = UserUtil.getUserPersonalId();
        Member member = memberService.validateMember(personalId);
        Channel channel = channelService.validateChannel(updateChannelDto.getChannelId());
        ChannelBoard channelBoard = validateChannelBoard(updateChannelDto.getChannelBoardId());

        validateChannelAndChannelBoard(channel, channelBoard);

        List<Participant> participantList = member.getParticipant();

        Optional<ChannelBoard> updateChannelBoard = participantList.stream()
                .filter(participant -> participant.getRole() == Role.HOST
                        && participant.getChannel().getId() == channel.getId())
                .map(participant -> ChannelBoard.updateChannelBoard(channelBoard, updateChannelDto))
                .findFirst();

        return updateChannelBoard;
    }

    @Transactional
    public void deleteChannelBoard(ChannelBoardDto deleteChannelBoardDto) {
        String personalId = UserUtil.getUserPersonalId();
        Member member = memberService.validateMember(personalId);
        Channel channel = channelService.validateChannel(deleteChannelBoardDto.getChannelId());
        ChannelBoard channelBoard = validateChannelBoard(deleteChannelBoardDto.getChannelBoardId());

        List<Participant> participantList = member.getParticipant();

        validateChannelAndChannelBoard(channel, channelBoard);

        participantList.stream()
                .filter(participant -> participant.getRole() == Role.HOST
                        && participant.getChannel() == channel)
                .forEach(participant -> channelBoardRepository.deleteById(channelBoard.getId()));

        return;
    }

    public ChannelBoard validateChannelBoard(Long channelBoardId) {
        return channelBoardRepository.findById(channelBoardId)
                .orElseThrow(ChannelBoardNotFoundException::new);
    }

    private void validateChannelAndChannelBoard(Channel channel, ChannelBoard channelBoard) {
        if(channel != channelBoard.getChannel()) {
            throw new ChannelBoardNotFoundException();
        }
    }


}
