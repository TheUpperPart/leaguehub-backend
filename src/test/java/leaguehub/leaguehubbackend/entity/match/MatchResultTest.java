package leaguehub.leaguehubbackend.entity.match;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.MatchFixture;
import leaguehub.leaguehubbackend.repository.match.MatchResultRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MatchResultTest {

    @Autowired
    MatchResultRepository matchResultRepository;

    @Test
    @DisplayName("순위 결과 생성 테스트")
    void createMatchResultTest() throws Exception {
        //given
        Channel channel = ChannelFixture.createDummyChannel(false, false, "Silver iv", 100);
        Match match = Match.createMatch(16, channel);
        MatchResult matchResult = MatchResult.createMatchResult("svkos12d0kr", match);
        MatchResult save = matchResultRepository.save(matchResult);



        assertThat(matchResult.getMatchCode()).isEqualTo(save.getMatchCode());
        assertThat(matchResult.getMatch().getMatchRound()).isEqualTo(save.getMatch().getMatchRound());
        assertThat(matchResult.getMatch().getMatchName()).isEqualTo(save.getMatch().getMatchName());
        assertThat(matchResult.getMatch().getMatchPasswd()).isEqualTo(save.getMatch().getMatchPasswd());

    }

}