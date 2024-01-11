package leaguehub.leaguehubbackend.service.match;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.match.MatchRoundListDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class MatchServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    ChannelBoardRepository channelBoardRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    ChannelRuleRepository channelRuleRepository;

    @Autowired
    MatchService matchService;
    @Autowired
    private MatchPlayerRepository matchPlayerRepository;


    @AfterEach
    public void tearDown() {
        participantRepository.deleteAll();
        matchRepository.deleteAll();
        channelRuleRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();
        channelBoardRepository.deleteAll();

        SecurityContextHolder.clearContext();
    }
    Channel createCustomChannel(Boolean tier, Boolean playCount, Integer tierMax, Integer tierMin, int playCountMin) throws Exception {
        Member member = memberRepository.save(UserFixture.createMember());
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("손성한"));
        Member masterMember = memberRepository.save(UserFixture.createCustomeMember("채수채수밭"));
        Member alreadyMember = memberRepository.save(UserFixture.createCustomeMember("요청한사람"));
        Member rejectedMember = memberRepository.save(UserFixture.createCustomeMember("거절된사람"));
        Member doneMember1 = memberRepository.save(UserFixture.createCustomeMember("참가된사람1"));
        Member doneMember2 = memberRepository.save(UserFixture.createCustomeMember("참가된사람2"));
        Member observer1 = memberRepository.save(UserFixture.createCustomeMember("관전자1"));
        Member observer2 = memberRepository.save(UserFixture.createCustomeMember("관전자2"));

        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(tier, playCount, tierMax, tierMin, playCountMin);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelRuleRepository.save(channelRule);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        participantRepository.save(Participant.participateChannel(ironMember, channel));
        participantRepository.save(Participant.participateChannel(platinumMember, channel));
        participantRepository.save(Participant.participateChannel(masterMember, channel));
        participantRepository.save(Participant.participateChannel(observer1, channel));
        participantRepository.save(Participant.participateChannel(observer2, channel));

        Participant alreadyParticipant = participantRepository.save(Participant.participateChannel(alreadyMember, channel));
        Participant rejectedParticipant = participantRepository.save(Participant.participateChannel(rejectedMember, channel));
        Participant doneParticipant1 = participantRepository.save(Participant.participateChannel(doneMember1, channel));
        Participant doneParticipant2 = participantRepository.save(Participant.participateChannel(doneMember2, channel));

        alreadyParticipant.updateParticipantStatus("participantGameId1", "bronze ii", "participantNickname1", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        rejectedParticipant.rejectParticipantRequest();
        doneParticipant1.updateParticipantStatus("participantGameId2", "platinum ii", "participantNickname2", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        doneParticipant2.updateParticipantStatus("participantGameId3", "iron ii", "participantNickname3", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        doneParticipant1.approveParticipantMatch();
        doneParticipant2.approveParticipantMatch();

        return channel;
    }

    @Test
    @DisplayName("매치 생성 테스트 - 성공")
    public void matchCreateSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(false, false, 2400, null, 20);

        matchService.createSubMatches(channel, channel.getMaxPlayer());

        List<Match> findMatchRound16 = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channel.getChannelLink(), 1);
        List<Match> findMatchRound8 = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channel.getChannelLink(), 2);

        assertThat(findMatchRound16.size()).isEqualTo(2);
        assertThat(findMatchRound8.size()).isEqualTo(1);
        assertThat(findMatchRound16.get(0).getMatchName()).isEqualTo("Group A");

    }


    @Test
    @DisplayName("라운드 리스트 조회 테스트 - 성공")
    public void matchListSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(false, false, 2400, null, 20);

        MatchRoundListDto roundList = matchService.getRoundList(channel.getChannelLink());

        assertThat(roundList.getRoundList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("DTO 변환 테스트")
    public void convertMatchPlayerDto() {
        List<MatchPlayer> matchPlayerList = matchPlayerRepository.findAllByMatch_IdOrderByPlayerScoreDesc(1184L);

        matchService.convertMatchPlayerInfoList(matchPlayerList).stream().forEach(matchPlayerInfo ->
                System.out.println(matchPlayerInfo.getGameId()+ " "
                + matchPlayerInfo.getScore() + " " + matchPlayerInfo.getMatchRank()));
    }

}