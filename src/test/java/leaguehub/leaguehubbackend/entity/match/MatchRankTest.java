package leaguehub.leaguehubbackend.entity.match;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.fixture.MatchFixture;
import leaguehub.leaguehubbackend.repository.match.MatchRankRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MatchRankTest {


    @Autowired
    MatchRankRepository matchRankRepository;


    @Test
    @DisplayName("순위 생성 테스트")
    void createMatchRankTest() throws Exception {
        Match match = MatchFixture.createMatch();
        MatchResult matchResult = MatchResult.createMatchResult("ab12345", match);
        MatchRank matchRank = MatchRank.createMatchRank("가나다", "1등", matchResult);
        MatchRank save = matchRankRepository.save(matchRank);


        assertThat(matchRank.getMatchResult().getMatchCode()).isEqualTo(save.getMatchResult().getMatchCode());
        assertThat(matchRank.getParticipant()).isEqualTo(save.getParticipant());
        assertThat(matchRank.getPlacement()).isEqualTo(save.getPlacement());

    }

}