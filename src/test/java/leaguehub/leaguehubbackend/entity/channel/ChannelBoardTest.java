package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static leaguehub.leaguehubbackend.fixture.ChannelFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelBoardTest {

    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ChannelBoardRepository channelBoardRepository;
    @Autowired
    ParticipantRepository participantRepository;

    public Channel createChannel() {
        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = createAllPropertiesCustomChannelDto(true, true, "Silver iv","Iron iv",100);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl(),
                channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));

        return channel;
    }


    @Test
    public void 채널_보드_기본생성_테스트() throws Exception {
        Channel channel = createChannel();
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());

        assertThat(channelBoards.size()).isEqualTo(3);
    }

    @Test
    public void 채널보드_생성_테스트() throws Exception {
        Channel channel = createChannel();
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());
        channelBoardRepository.save(ChannelBoard.createChannelBoard(channel, createChannelBoardDto().getTitle(),
                createChannelBoardDto().getContent(), channelBoards.size()));
        channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());

        assertThat(channelBoards.size()).isEqualTo(4);
    }


    @Test
    public void 채널보드_업데이트_테스트() throws Exception {
        Channel channel = createChannel();
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());
        ChannelBoard saved = channelBoardRepository.save(ChannelBoard.createChannelBoard(channel, createChannelBoardDto().getTitle(),
                createChannelBoardDto().getContent(), channelBoards.size()));
        channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());
        ChannelBoardDto updateChannelBoardDto = updateChannelBoardDto();


        channelBoardRepository.save(saved.updateChannelBoard(updateChannelBoardDto.getTitle(), updateChannelBoardDto.getContent()));
        channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.getId());

        assertThat(channelBoards.size()).isEqualTo(4);
        assertThat(channelBoards.get(channelBoards.size() - 1).getTitle()).isEqualTo("test1");
    }
}