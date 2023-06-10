package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelCreateException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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


    @Test
    @DisplayName("채널 생성 테스트 - 티어 제한, 판수제한 X")
    public void 채널_생성_테스트() throws Exception {
        //given
        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = ChannelFixture.createChannelDto();
        Channel channel = Channel.createChannel(channelDto, member);
        channelRepository.save(channel);
        Channel.createParticipationLink(channel);

        //when
        Optional<Channel> findChannel = channelRepository.findById(channel.getId());

        //then
        assertThat(findChannel.get().getParticipant().get(0).getMember()).isEqualTo(member);
        assertThat(findChannel.get().getChannelStatus()).isEqualTo(ChannelStatus.PREPARING);
        assertThat(findChannel.get().getRealPlayer()).isEqualTo(0);
        assertThat(findChannel.get().getCategory()).isEqualTo(Category.getByNumber(channelDto.getGame()));
        assertThat(findChannel.get().getChannelRule().getLimitedPlayCount()).isEqualTo(Integer.MAX_VALUE);
        assertThat(findChannel.get().getChannelBoards().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("채널 생성 테스트 - 티어, 판수 제한 O")
    public void 채널_생성_테스트_제한() throws Exception {
        //given
        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesChannelDto();
        Channel channel = Channel.createChannel(channelDto, member);
        channelRepository.save(channel);
        Channel.createParticipationLink(channel);

        //when
        Optional<Channel> findChannel = channelRepository.findById(channel.getId());

        //then
        assertThat(findChannel.get().getParticipant().get(0).getMember()).isEqualTo(member);
        assertThat(findChannel.get().getChannelStatus()).isEqualTo(ChannelStatus.PREPARING);
        assertThat(findChannel.get().getRealPlayer()).isEqualTo(0);
        assertThat(findChannel.get().getCategory()).isEqualTo(Category.getByNumber(channelDto.getGame()));
        assertThat(findChannel.get().getChannelRule().getLimitedPlayCount()).isEqualTo(channelDto.getPlayCountMin());
        assertThat(findChannel.get().getParticipationLink()).isEqualTo("http://localhost:8080/" + channel.getId());
        assertThat(findChannel.get().getChannelBoards().size()).isEqualTo(3);
    }

    @Test
    public void 유효하지않는_채널_테스트_게임카테고리() throws Exception {
        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = ChannelFixture.invalidatedCategoryData();
        assertThatThrownBy(() -> Channel.createChannel(channelDto, member))
                .isInstanceOf(ChannelCreateException.class);
    }

    @Test
    public void 유효하지않는_채널_테스트_토너먼트형식() throws Exception {
        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = ChannelFixture.invalidatedTournamentData();
        assertThatThrownBy(() -> Channel.createChannel(channelDto, member))
                .isInstanceOf(ChannelCreateException.class);
    }

}