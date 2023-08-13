package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelRuleDto;
import leaguehub.leaguehubbackend.dto.channel.ParticipantChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelRuleServiceTest {

    @Autowired
    ChannelRuleService channelRuleService;
    @Autowired
    ChannelRuleRepository channelRuleRepository;
    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    MemberRepository memberRepository;

    Channel channel;

    @BeforeEach
    void setUp() {
        memberRepository.save(UserFixture.createMember());
        UserFixture.setUpAuth();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(ChannelFixture.createChannelDto());

        Optional<Channel> findChannel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        channel = findChannel.get();
    }

    @Test
    @DisplayName("채널 룰 가져오기 테스트")
    void getChannelRuleTest() {
        ChannelRuleDto channelRule = channelRuleService.getChannelRule(channel.getChannelLink());
        assertThat(channelRule.getTier()).isFalse();
        assertThat(channelRule.getPlayCount()).isFalse();
    }

    @Test
    @DisplayName("채널 룰 업데이트 테스트")
    void updateChannelRuleTest() {
        ChannelRuleDto channelRuleDto = ChannelFixture.updateChannelRule();
        channelRuleService.updateChannelRule(channel.getChannelLink(), channelRuleDto);
        assertThat(channel.getChannelRule().getTierMax()).isEqualTo(channelRuleDto.getTierMax());
        assertThat(channel.getChannelRule().getTierMin()).isEqualTo(channelRuleDto.getTierMin());
        assertThat(channel.getChannelRule().getLimitedPlayCount()).isEqualTo(channelRuleDto.getPlayCountMin());
    }

    @Test
    @DisplayName("채널 룰 업데이트 테스트 - 실패(티어 유효성)")
    void updateChannelRuleTest_tierValid() {
        ChannelRuleDto channelRuleDto = ChannelFixture.updateChannelRule();
        channelRuleDto.setTierMax(null);
        channelRuleDto.setTierMin(null);
        assertThatThrownBy(() -> channelRuleService.updateChannelRule(channel.getChannelLink(), channelRuleDto))
                .isInstanceOf(ChannelRequestException.class);
    }

    @Test
    @DisplayName("채널 룰 업데이트 테스트 - 실패(판수 유효성)")
    void updateChannelRuleTest_playCountValid() {
        ChannelRuleDto channelRuleDto = ChannelFixture.updateChannelRule();
        channelRuleDto.setPlayCountMin(null);
        assertThatThrownBy(() -> channelRuleService.updateChannelRule(channel.getChannelLink(), channelRuleDto))
                .isInstanceOf(ChannelRequestException.class);
    }

    @Test
    @DisplayName("채널 룰 업데이트 테스트 - 권한 없음")
    void updateChannelRuleTest_noAuth() {
        ChannelRuleDto channelRuleDto = ChannelFixture.updateChannelRule();
        memberRepository.save(UserFixture.createCustomeMember("test1"));
        UserFixture.setUpCustomAuth("test1");

        assertThatThrownBy(() -> channelRuleService.updateChannelRule(channel.getChannelLink(), channelRuleDto))
                .isInstanceOf(InvalidParticipantAuthException.class);
    }

}