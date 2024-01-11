package leaguehub.leaguehubbackend.entity.participant;

import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus;
import leaguehub.leaguehubbackend.domain.participant.entity.Role;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
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
    @Autowired
    private ChannelRuleRepository channelRuleRepository;


    @Test
    @DisplayName("채널 생성시 유저는 호스트 권한을 가진 참가자가 된다.")
    public void createHostTest() throws Exception {

        Member member = memberRepository.save(UserFixture.createMember());
        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(true, true, 800, null, 100);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());

        ChannelRule channelRule = ChannelRule.createChannelRule(channel,
                channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelRuleRepository.save(channelRule);
        Participant participant = participantRepository.save(Participant.createHostChannel(member, channel));


        assertThat(participantRepository.findAllByChannel_ChannelLink(channel.getChannelLink()).size()).isEqualTo(1);
        assertThat(participant.getChannel()).isEqualTo(channel);
        assertThat(participant.getMember()).isEqualTo(member);
        assertThat(participant.getRole()).isEqualTo(Role.HOST);

        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.NO_REQUEST);
        assertThat(participant.getGameId()).isEqualTo(GlobalConstant.NO_DATA.getData());
    }

}