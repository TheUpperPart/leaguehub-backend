package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.UpdateChannelBoardDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static leaguehub.leaguehubbackend.fixture.ChannelFixture.*;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelBoardTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ChannelBoardRepository channelBoardRepository;
    @Autowired
    ParticipantRepository participantRepository;

    public Channel createChannel() {
        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = createAllPropertiesChannelDto();
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGame(), channelDto.getParticipationNum(),
                channelDto.getTournament(), channelDto.getChannelImageUrl(),
                channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        channel.createParticipationLink();

        return channel;
    }


    @Test
    public void 채널_보드_기본생성_테스트() throws Exception {
        Channel channel = createChannel();
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel);

        assertThat(channelBoards.size()).isEqualTo(3);
    }

    @Test
    public void 채널보드_생성_테스트() throws Exception {
        Channel channel = createChannel();
        channelBoardRepository.save(ChannelBoard.createChannelBoard(channel, createChannelBoardDto().getTitle(),
                createChannelBoardDto().getContent()));
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel);

        assertThat(channelBoards.size()).isEqualTo(4);
    }


    @Test
    public void 채널보드_업데이트_테스트() throws Exception {
        Channel channel = createChannel();
        ChannelBoard saved = channelBoardRepository.save(ChannelBoard.createChannelBoard(channel
                , createChannelBoardDto().getTitle(), createChannelBoardDto().getContent()));
        UpdateChannelBoardDto updateChannelBoardDto = updateChannelDto();
        updateChannelBoardDto.setChannelId(saved.getId());
        updateChannelBoardDto.setChannelBoardId(saved.getId());

        channelBoardRepository.save(saved.updateChannelBoard(updateChannelBoardDto.getTitle(), updateChannelBoardDto.getContent()));
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel);

        assertThat(channelBoards.size()).isEqualTo(4);
        assertThat(channelBoards.get(channelBoards.size() - 1).getTitle()).isEqualTo("test1");
    }
}