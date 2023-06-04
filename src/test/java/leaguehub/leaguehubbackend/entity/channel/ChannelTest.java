package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.member.MemberNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.ChannelService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

    Member createMember() {
        Member member = Member.builder()
                .personalId("id").profileImageUrl("url")
                .nickname("test").build();

        return member;
    }

    CreateChannelDto createChannelDto() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .game(0)
                .title("test")
                .tournament(0)
                .participationNum(16)
                .tier(false)
                .playCount(false)
                .build();


        return createChannelDto;
    }

    @Test
    @DisplayName("채널 생성 테스트")
    public void 채널_생성_테스트() throws Exception {
        //given
        Member member = memberRepository.save(createMember());
        CreateChannelDto channelDto = createChannelDto();
        Long channelId = channelService.createChannel(channelDto, member.getPersonalId());

        //when
        Optional<Channel> findChannel = channelRepository.findById(channelId);

        //then
        assertThat(findChannel.get().getParticipant().get(0).getMember()).isEqualTo(member);
        assertThat(findChannel.get().getChannelStatus()).isEqualTo(ChannelStatus.PREPARING);
        assertThat(findChannel.get().getRealPlayer()).isEqualTo(0);
        assertThat(findChannel.get().getCategory()).isEqualTo(Category.getByNumber(createChannelDto().getGame()));
    }

    @Test
    @DisplayName("채널 생성시 유저를 조회 후 정보가 없으면 예외를 반환한다.")
    public void 채널_생성_유저_예외() throws Exception {
        assertThatThrownBy(() ->
                        channelService.createChannel(createChannelDto(), "test"))
                .isInstanceOf(MemberNotFoundException.class);

    }

}