package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.domain.channel.dto.ChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.UpdateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelStatus;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
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

import java.util.List;
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
    @Autowired
    ChannelRuleRepository channelRuleRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(UserFixture.createMember());
        UserFixture.setUpAuth();
    }

    @Test
    @DisplayName("채널 생성 테스트 - 서비스")
    void createChannel() {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);

        Optional<Channel> findChannel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(findChannel.get().getChannelLink());
        assertThat(findChannel.get().getChannelStatus()).isEqualTo(ChannelStatus.PREPARING);
        assertThat(findChannel.get().getMaxPlayer()).isEqualTo(createChannelDto.getMaxPlayer());
        assertThat(findChannel.get().getTitle()).isEqualTo(createChannelDto.getTitle());
        assertThat(channelRule.getTier()).isFalse();
    }

    @Test
    @DisplayName("채널 생성 실패 테스트 - 서비스(티어 유효성)")
    void createChannel_TierValid() {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        createChannelDto.setTier(true);
        assertThatThrownBy(() -> channelService.createChannel(createChannelDto))
                .isInstanceOf(ChannelRequestException.class);
    }

    @Test
    @DisplayName("채널 생성 실패 테스트 - 서비스(판수 유효성)")
    void createChannel_PlayCountValid() {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        createChannelDto.setPlayCount(true);
        createChannelDto.setPlayCountMin(null);
        assertThatThrownBy(() -> channelService.createChannel(createChannelDto))
                .isInstanceOf(ChannelRequestException.class);
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
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);

        Optional<Channel> findChannel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        String validateChannelLink = findChannel.get().getChannelLink();
        ChannelDto channel = channelService.findChannel(validateChannelLink);

        assertThat(channel.getTitle()).isEqualTo(findChannel.get().getTitle());
        assertThat(channel.getRealPlayer()).isEqualTo(findChannel.get().getRealPlayer());
    }

    @Test
    @DisplayName("참가한 채널 찾기")
    void findParticipantList() {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        CreateChannelDto createChannelDto1 = ChannelFixture.createChannelDto();
        createChannelDto1.setTitle("test3");
        ParticipantChannelDto participantChannelDto1 = channelService.createChannel(createChannelDto1);

        List<ParticipantChannelDto> participantChannelList = channelService.findParticipantChannelList();

        assertThat(participantChannelList.size()).isEqualTo(2);
    }


    @Test
    @DisplayName("채널 업데이트 - 제목, 참가자수, 채널 이미지")
    void updateChannel() {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        UpdateChannelDto updateChannelDto = ChannelFixture.updateChannelDto();

        channelService.updateChannel(participantChannelDto.getChannelLink(), updateChannelDto);

        Optional<Channel> findChannel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        assertThat(findChannel.get().getTitle()).isEqualTo(updateChannelDto.getTitle());
        assertThat(findChannel.get().getMaxPlayer()).isEqualTo(updateChannelDto.getMaxPlayer());
        assertThat(findChannel.get().getChannelImageUrl()).isEqualTo(updateChannelDto.getChannelImageUrl());

    }

}