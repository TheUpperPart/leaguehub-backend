package leaguehub.leaguehubbackend.entity.participant;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
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
        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(true, true, "Silver iv", null, 100);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGame(), channelDto.getParticipationNum(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl(),
                channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        Participant participant = participantRepository.save(Participant.createHostChannel(member, channel));


        assertThat(participantRepository.findAll().size()).isEqualTo(1);
        assertThat(participant.getChannel()).isEqualTo(channel);
        assertThat(participant.getMember()).isEqualTo(member);
        assertThat(participant.getRole()).isEqualTo(Role.HOST);

        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.NO_REQUEST);
        assertThat(participant.getGameId()).isEqualTo(GlobalConstant.NO_DATA.getData());
    }

}