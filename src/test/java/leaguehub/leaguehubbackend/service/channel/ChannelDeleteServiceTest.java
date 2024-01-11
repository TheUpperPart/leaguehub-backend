package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.channel.ChannelStatus;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelStatusAlreadyException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantNotGameHostException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelDeleteServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    ChannelBoardService channelBoardService;
    @Autowired
    ChannelBoardRepository channelBoardRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    ChannelRuleRepository channelRuleRepository;
    @Autowired
    ChannelDeleteService channelDeleteService;
    @Autowired
    MatchRepository matchRepository;
    @Autowired
    MatchPlayerRepository matchPlayerRepository;
    private Member member1;

    private Match savedMatch;
    private Channel channel;

    @BeforeEach
    public void setUp() {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username("member1")
                .password("member1")
                .roles("USER")
                .build();

        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null
                , authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        member1 = memberRepository.save(UserFixture.createCustomeMember("member1"));
        Member member2 = memberRepository.save(UserFixture.createCustomeMember("member2"));
        Member member3 = memberRepository.save(UserFixture.createCustomeMember("member3"));
        Member member4 = memberRepository.save(UserFixture.createCustomeMember("member4"));

        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(false, false, 2400, null, 20);
        channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelRuleRepository.save(channelRule);

        // Participant 생성 및 저장
        Participant participant1 = participantRepository.save(Participant.createHostChannel(member1, channel));
        Participant participant2 = participantRepository.save(Participant.participateChannel(member2, channel));
        Participant participant3 = participantRepository.save(Participant.participateChannel(member3, channel));
        Participant participant4 = participantRepository.save(Participant.participateChannel(member4, channel));

        // Match 생성 및 저장
        Integer matchRound = 1;
        String matchName = "Sample Match";
        Match match = Match.createMatch(matchRound, channel, matchName);
        savedMatch = matchRepository.save(match);

        // MatchPlayer 생성
        MatchPlayer matchPlayer1 = MatchPlayer.createMatchPlayer(participant1, savedMatch);
        MatchPlayer matchPlayer2 = MatchPlayer.createMatchPlayer(participant2, savedMatch);
        MatchPlayer matchPlayer3 = MatchPlayer.createMatchPlayer(participant3, savedMatch);
        MatchPlayer matchPlayer4 = MatchPlayer.createMatchPlayer(participant4, savedMatch);
        matchPlayer1.updateMatchPlayerScore(1);
        matchPlayer2.updateMatchPlayerScore(2);
        matchPlayer3.updateMatchPlayerScore(2);
        matchPlayer4.updateMatchPlayerScore(3);

        matchPlayerRepository.save(matchPlayer1);
        matchPlayerRepository.save(matchPlayer2);
        matchPlayerRepository.save(matchPlayer3);
        matchPlayerRepository.save(matchPlayer4);
    }

    @Test
    void deleteChannel() {
        channelDeleteService.deleteChannel(channel.getChannelLink());

        List<Participant> allByMemberId = participantRepository.findAllByMemberId(member1.getId());

        Assertions.assertThat(allByMemberId.size()).isEqualTo(0);

        Assertions.assertThatThrownBy(() -> channelRepository.findByChannelLink(channel.getChannelLink()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteChannel_fail_not_auth() {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username("member2")
                .password("member2")
                .roles("USER")
                .build();

        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null
                , authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Assertions.assertThatThrownBy(() -> channelDeleteService.deleteChannel(channel.getChannelLink()))
                .isInstanceOf(ParticipantNotGameHostException.class);
    }

    @Test
    void deleteChannel_fail() {
        channel.updateChannelStatus(ChannelStatus.PROCEEDING);

        Assertions.assertThatThrownBy(() -> channelDeleteService.deleteChannel(channel.getChannelLink()))
                .isInstanceOf(ChannelStatusAlreadyException.class);
    }
}