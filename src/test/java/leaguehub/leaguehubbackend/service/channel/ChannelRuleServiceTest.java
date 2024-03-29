package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.domain.channel.dto.ChannelRuleDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelRuleService;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
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
        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(channel.getChannelLink());
        assertThat(channelRule.getTierMax()).isEqualTo(channelRuleDto.getTierMax());
        assertThat(channelRule.getTierMin()).isEqualTo(channelRuleDto.getTierMin());
        assertThat(channelRule.getLimitedPlayCount()).isEqualTo(channelRuleDto.getPlayCountMin());
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