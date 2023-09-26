package leaguehub.leaguehubbackend.service.match;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.match.MatchPlayerScoreInfo;
import leaguehub.leaguehubbackend.dto.match.MatchScoreInfoDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.fixture.MatchScoreListFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
public class MatchServiceScoreTest {

    @Autowired
    private MatchService matchService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    ChannelRuleRepository channelRuleRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    MatchPlayerRepository matchPlayerRepository;
    @Autowired
    MatchRepository matchRepository;
    @Mock
    MemberService memberService;

    private Member member1;

    private Match savedMatch;

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
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelRuleRepository.save(channelRule);

        // Participant 생성 및 저장
        Participant participant1 = participantRepository.save(Participant.createHostChannel(member1, channel));
        Participant participant2 = participantRepository.save(Participant.createHostChannel(member2, channel));
        Participant participant3 = participantRepository.save(Participant.createHostChannel(member3, channel));
        Participant participant4 = participantRepository.save(Participant.createHostChannel(member4, channel));

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

    @AfterEach
    public void tearDown() {
        matchPlayerRepository.deleteAll();
        participantRepository.deleteAll();
        matchRepository.deleteAll();
        channelRuleRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getMatchScoreInfo 테스트 - 성공")
    public void getMatchScoreInfoSuccessTest() throws Exception {

        MatchScoreInfoDto result = matchService.getMatchScoreInfo(savedMatch.getId());

        assertNotNull(result);

        List<MatchPlayerScoreInfo> scoreInfos = result.getMatchPlayerScoreInfos();
        assertNotNull(scoreInfos);
        assertEquals(4, scoreInfos.size());

    }

    @Test
    @DisplayName("getMatchScoreInfo 테스트 - 유효하지 않은 matchId")
    public void getMatchScoreInfoInvalidMatchIdTest() {

        Long invalidMatchId = 1234L;

        assertThrows(MatchNotFoundException.class, () -> {
            matchService.getMatchScoreInfo(invalidMatchId);
        });
    }

}