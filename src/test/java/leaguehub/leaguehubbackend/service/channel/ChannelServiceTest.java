package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelStatus;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
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
class ChannelServiceTest {

    @Autowired
    ChannelService channelService;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(UserFixture.createMember());
        UserFixture.setUpAuth();
    }

    @Test
    @DisplayName("채널 생성 테스트 - 서비스")
    void createChannel() {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        Long channelId = channelService.createChannel(createChannelDto);

        Optional<Channel> findChannel = channelRepository.findById(channelId);
        assertThat(findChannel.get().getChannelStatus()).isEqualTo(ChannelStatus.PREPARING);
        assertThat(findChannel.get().getMaxPlayer()).isEqualTo(createChannelDto.getParticipationNum());
        assertThat(findChannel.get().getTitle()).isEqualTo(createChannelDto.getTitle());
    }

    @Test
    @DisplayName("findChannel 실패 - 서비스")
    void invalidateTest() {
        String validateChannelLink = "NoValid";
        assertThatThrownBy(() -> channelService.findChannel(validateChannelLink))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("findChannel 성공 - 서비스")
    void validateTest() {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        Long channelId = channelService.createChannel(createChannelDto);

        Optional<Channel> findChannel = channelRepository.findById(channelId);

        String validateChannelLink = findChannel.get().getChannelLink();
        ChannelDto channel = channelService.findChannel(validateChannelLink);

        assertThat(channel.getTitle()).isEqualTo(findChannel.get().getTitle());
        assertThat(channel.getRealPlayer()).isEqualTo(findChannel.get().getRealPlayer());
    }

}