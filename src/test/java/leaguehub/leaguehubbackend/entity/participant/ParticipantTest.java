package leaguehub.leaguehubbackend.entity.participant;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ParticipantTest {

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
    @DisplayName("채널 생성시 유저는 호스트 권한을 가진 참가자가 된다.")
    public void createHostTest() throws Exception {

        Member member = memberRepository.save(createMember());

        Long id = channelService.createChannel(createChannelDto(), "id");
        Optional<Channel> findChannel = channelRepository.findById(id);
        List<Participant> participants = findChannel.get().getParticipant();

        Participant participant = participants.get(0);


        assertThat(participants.size()).isEqualTo(1);
        assertThat(participant.getMember()).isEqualTo(member);
        assertThat(participant.getRole()).isEqualTo(Role.HOST);
        assertThat(participant.getGameId()).isEqualTo(GlobalConstant.NO_DATA.getData());
    }

}