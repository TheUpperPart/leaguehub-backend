package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardDto;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelBoard;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelBoardRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
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
    @Autowired
    private ChannelRuleRepository channelRuleRepository;

    public Channel createChannel() {
        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = createAllPropertiesCustomChannelDto(true, true, 800, 0, 100);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());

        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelRuleRepository.save(channelRule);
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