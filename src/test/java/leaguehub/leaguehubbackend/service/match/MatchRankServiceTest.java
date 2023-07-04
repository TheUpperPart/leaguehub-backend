package leaguehub.leaguehubbackend.service.match;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.exception.match.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import leaguehub.leaguehubbackend.fixture.MatchFixture;
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



    @Test
    @DisplayName("경기 검색 성공 테스트")
    void searchMatchSuccessTest() throws Exception {
        //given
        Match match = MatchFixture.createMatch();
        matchRepository.save(match);
        MatchResponseDto matchResponseDto = MatchFixture.createMatchResponseDto();
        List<MatchRankResultDto> save = matchRankService.setMatchRank(matchResponseDto);

        List<MatchRankResultDto> resultList = matchRankService.getMatchDetail("KR_6519793792");

        assertThat(save).isEqualTo(resultList);
    }

    @Test
    @DisplayName("경기 검색 실패 테스트")
    void searchMatchFailTest() throws Exception {
        //given
        Match match = MatchFixture.createMatch();
        matchRepository.save(match);
        MatchResponseDto matchResponseDto = MatchFixture.createFailResponseDto();

        assertThatThrownBy(() -> matchRankService.setMatchRank(matchResponseDto))
                .isInstanceOf(ParticipantGameIdNotFoundException.class);
    }
}