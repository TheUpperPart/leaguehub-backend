package leaguehub.leaguehubbackend.entity.participant;

import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ParticipantTest {

    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ParticipantRepository participantRepository;


    @Test
    @DisplayName("채널 생성시 유저는 호스트 권한을 가진 참가자가 된다.")
    public void createHostTest() throws Exception {

        Member member = memberRepository.save(UserFixture.createMember());

        Channel channel = Channel.createChannel(ChannelFixture.createChannelDto(), member);
        Channel savedChannel = channelRepository.save(channel);

        List<Participant> participantList = savedChannel.getParticipant();
        Participant participant = participantList.get(0);
        Participant findParticipant = participantRepository.findParticipantById(participant.getId());


        assertThat(participantList.size()).isEqualTo(1);
        assertThat(findParticipant.getId()).isEqualTo(participant.getId());
        assertThat(findParticipant.getMember()).isEqualTo(member);
        assertThat(participant.getMember()).isEqualTo(member);
        assertThat(participant.getRole()).isEqualTo(Role.HOST);
        assertThat(participant.getGameId()).isEqualTo(GlobalConstant.NO_DATA.getData());
    }

}