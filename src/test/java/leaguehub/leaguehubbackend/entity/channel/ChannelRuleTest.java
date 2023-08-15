package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelRuleTest {

    @Autowired
    ChannelRuleRepository channelRuleRepository;

    @Test
    @DisplayName("채널 룰 생성 테스트")
    public void createChannelRule() throws Exception {
        CreateChannelDto channelDto = ChannelFixture.createChannelDto();
        Channel channel = ChannelFixture.createDummyChannel(false, false, null, null, 100);
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(), channelDto.getPlayCount(), channelDto.getPlayCountMin());

        ChannelRule save = channelRuleRepository.save(channelRule);

        assertThat(channelRule.getLimitedPlayCount()).isEqualTo(save.getLimitedPlayCount());
        assertThat(channelRule.getTier()).isEqualTo(save.getTier()).isEqualTo(channelDto.getTier());
    }

    @Test
    @DisplayName("채널 룰 업데이트 테스트 - 티어")
    public void updateChannelRule_Tier() {
        CreateChannelDto channelDto = ChannelFixture.createChannelDto();
        Channel channel = ChannelFixture.createDummyChannel(false, false, null, null, 100);
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(), channelDto.getPlayCount(), channelDto.getPlayCountMin());
        channelRuleRepository.save(channelRule);
        channelRule.updateTierRule(true, 800, null);

        assertThat(channelRule.getTierMax()).isEqualTo(800);
        assertThat(channelRule.getTierMin()).isEqualTo(Integer.MIN_VALUE);

        channelRule.updateTierRule(false);

        assertThat(channelRule.getTier()).isEqualTo(false);
    }


    @Test
    @DisplayName("채널 룰 업데이트 테스트 - 판수")
    public void updateChannelRule_PlayCount() {
        CreateChannelDto channelDto = ChannelFixture.createChannelDto();
        Channel channel = ChannelFixture.createDummyChannel(false, false, null, null, 100);
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(), channelDto.getPlayCount(), channelDto.getPlayCountMin());
        channelRuleRepository.save(channelRule);
        channelRule.updatePlayCountMin(true, 100);
        assertThat(channelRule.getLimitedPlayCount()).isEqualTo(100);

        channelRule.updatePlayCountMin(false);
        assertThat(channelRule.getPlayCount()).isFalse();
    }
}