package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

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
        ChannelRule channelRule = ChannelRule.createChannelRule(channelDto.getTierMax(), channelDto.getGradeMax(), channelDto.getTier(),
                channelDto.getPlayCount(), channelDto.getPlayCountMin());

        ChannelRule save = channelRuleRepository.save(channelRule);

        assertThat(channelRule.getLimitedPlayCount()).isEqualTo(save.getLimitedPlayCount());
        assertThat(channelRule.getTier()).isEqualTo(save.getTier()).isEqualTo(channelDto.getTier());
    }
}