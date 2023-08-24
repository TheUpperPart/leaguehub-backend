package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelTest {

    @Autowired
    ChannelService channelService;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ChannelBoardRepository channelBoardRepository;
    @Autowired
    ParticipantRepository participantRepository;

    Member member;
    @Autowired
    private ChannelRuleRepository channelRuleRepository;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(UserFixture.createMember());
    }

    @Test
    @DisplayName("채널 생성 테스트 - 티어 제한, 판수제한 X")
    public void 채널_생성_테스트() throws Exception {
        //given
        CreateChannelDto channelDto = ChannelFixture.createChannelDto();
        Channel channel = Channel.createChannel(channelDto.getTitle(), channelDto.getGameCategory(), channelDto.getMaxPlayer(), channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(), channelDto.getPlayCount(), channelDto.getPlayCountMin());
        channelRepository.save(channel);
        ChannelRule saveChannelRule = channelRuleRepository.save(channelRule);
        List<ChannelBoard> channelBoardList = channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        Participant participant = participantRepository.save(Participant.createHostChannel(member, channel));

        //when
        Optional<Channel> findChannel = channelRepository.findById(channel.getId());

        //then
        assertThat(participant.getMember()).isEqualTo(member);
        assertThat(participant.getChannel()).isEqualTo(channel);
        assertThat(findChannel.get().getChannelStatus()).isEqualTo(ChannelStatus.PREPARING);
        assertThat(findChannel.get().getRealPlayer()).isEqualTo(0);
        assertThat(findChannel.get().getGameCategory()).isEqualTo(GameCategory.getByNumber(channelDto.getGameCategory()));
        assertThat(saveChannelRule.getTierMax()).isEqualTo(Integer.MIN_VALUE);
        assertThat(channelBoardList.size()).isEqualTo(3);
        assertThat(channelBoardList.get(0).getChannel()).isEqualTo(channel);
    }

    @Test
    @DisplayName("채널 생성 테스트 - 티어, 판수 제한 O")
    public void 채널_생성_테스트_제한() throws Exception {
        //given
        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(true, true, 800, 400, 100);
        Channel channel = Channel.createChannel(channelDto.getTitle(), channelDto.getGameCategory(), channelDto.getMaxPlayer(), channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        channelRepository.save(channel);
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(), channelDto.getPlayCount(), channelDto.getPlayCountMin());
        ChannelRule saveChannelRule = channelRuleRepository.save(channelRule);
        List<ChannelBoard> channelBoardList = channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        Participant participant = participantRepository.save(Participant.createHostChannel(member, channel));

        //when
        Optional<Channel> findChannel = channelRepository.findById(channel.getId());

        //then
        assertThat(participant.getMember()).isEqualTo(member);
        assertThat(participant.getChannel()).isEqualTo(channel);
        assertThat(findChannel.get().getChannelStatus()).isEqualTo(ChannelStatus.PREPARING);
        assertThat(findChannel.get().getRealPlayer()).isEqualTo(0);
        assertThat(findChannel.get().getGameCategory()).isEqualTo(GameCategory.getByNumber(channelDto.getGameCategory()));
        assertThat(saveChannelRule.getLimitedPlayCount()).isEqualTo(channelDto.getPlayCountMin());
        assertThat(channelBoardList.size()).isEqualTo(3);
        assertThat(channelBoardList.get(0).getChannel()).isEqualTo(channel);
        assertThat(findChannel.get().getChannelLink()).isEqualTo(channel.getChannelLink());
        assertThat(saveChannelRule.getTierMax()).isEqualTo(800);
    }

    @Test
    public void 유효하지않는_채널_테스트_게임카테고리() throws Exception {
        CreateChannelDto channelDto = ChannelFixture.invalidatedCategoryData();
        assertThatThrownBy(() -> Channel.createChannel(channelDto.getTitle(), channelDto.getGameCategory(), channelDto.getMaxPlayer(), channelDto.getMatchFormat(), channelDto.getChannelImageUrl())).isInstanceOf(ChannelRequestException.class);
    }

    @Test
    public void 유효하지않는_채널_테스트_토너먼트형식() throws Exception {
        CreateChannelDto channelDto = ChannelFixture.invalidatedTournamentData();
        assertThatThrownBy(() -> Channel.createChannel(channelDto.getTitle(), channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl())).isInstanceOf(ChannelRequestException.class);
    }

    @Test
    public void update_test() {
        CreateChannelDto channelDto = ChannelFixture.createChannelDto();
        Channel channel = Channel.createChannel(channelDto.getTitle(), channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        channelRepository.save(channel);
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(), channelDto.getPlayCount(), channelDto.getPlayCountMin());
        ChannelRule saveChannelRule = channelRuleRepository.save(channelRule);
        channel.updateMaxPlayer(64);
        channel.updateChannelImageUrl("test");
        channel.updateTitle("test123");
        Optional<Channel> findChannel = channelRepository.findByChannelLink(channel.getChannelLink());

        assertThat(findChannel.get().getMaxPlayer()).isEqualTo(64);
        assertThat(findChannel.get().getTitle()).isEqualTo("test123");
        assertThat(findChannel.get().getChannelImageUrl()).isEqualTo("test");
    }

}