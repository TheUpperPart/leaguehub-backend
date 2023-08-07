package leaguehub.leaguehubbackend.service.match;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.match.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.MatchFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRankRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.match.MatchResultRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MatchRankServiceTest {

    @Autowired
    MatchRankService matchRankService;
    @Autowired
    MatchResultService matchResultService;
    @Autowired
    MatchRankRepository matchRankRepository;
    @Autowired
    MatchResultRepository matchResultRepository;
    @Autowired
    MatchRepository matchRepository;
    @Autowired
    ChannelRepository channelRepository;

    @Test
    @DisplayName("경기 검색 테스트 - 성공")
    void searchMatchSuccessTest() throws Exception {
        //given
        Channel channel = ChannelFixture.createDummyChannel(false, false, "Silver iv", 100);
        channelRepository.save(channel);
        Match match = Match.createMatch(16, channel);
        matchRepository.save(match);
        MatchResponseDto matchResponseDto = MatchFixture.createMatchResponseDto(match.getMatchLink());
        List<MatchRankResultDto> save = matchRankService.setMatchRank(matchResponseDto);

        List<MatchRankResultDto> resultList = matchRankService.getMatchDetail("KR_6519793792");

        assertThat(save).isEqualTo(resultList);
    }

    @Test
    @DisplayName("경기 검색 테스트 - 실패")
    void searchMatchFailTest() throws Exception {
        //given
        Channel channel = ChannelFixture.createDummyChannel(false, false, "Silver iv", 100);
        channelRepository.save(channel);
        Match match = Match.createMatch(16, channel);
        matchRepository.save(match);
        MatchResponseDto matchResponseDto = MatchFixture.createFailResponseDto(match.getMatchLink());

        assertThatThrownBy(() -> matchRankService.setMatchRank(matchResponseDto))
                .isInstanceOf(ParticipantGameIdNotFoundException.class);
    }
}