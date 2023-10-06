package leaguehub.leaguehubbackend.service.match;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.match.GameResultDto;
import leaguehub.leaguehubbackend.dto.match.MatchInfoDto;
import leaguehub.leaguehubbackend.dto.match.MatchPlayerInfo;
import leaguehub.leaguehubbackend.entity.match.MatchPlayerResultStatus;
import leaguehub.leaguehubbackend.entity.match.MatchStatus;
import leaguehub.leaguehubbackend.entity.match.PlayerStatus;
import leaguehub.leaguehubbackend.exception.match.exception.MatchAlreadyUpdateException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRankRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class MatchPlayerServiceTest {

    @Autowired
    MatchPlayerRepository matchPlayerRepository;
    @Autowired
    MatchRankRepository matchRankRepository;
    @Autowired
    MatchRepository matchRepository;
    @Autowired
    MatchPlayerService matchPlayerService;

    @Test
    @DisplayName("점수 업데이트 성공 - 동점자 로직(1등 많이 한 사람, Becrux) 진출")
    void updateMatchPlayerScore() {
        MatchInfoDto matchInfoDto = matchPlayerService.updateMatchPlayerScore(1L, 3, 1691910300L);

        assertThat(matchInfoDto.getMatchStatus()).isEqualTo(MatchStatus.END);

        Optional<MatchPlayerInfo> 무진스_협회장 = matchInfoDto.getMatchPlayerInfoList().stream()
                .filter(matchPlayerInfo -> matchPlayerInfo.getGameId().equals("무진스 협회장"))
                .findFirst();
        assertThat(무진스_협회장.get().getMatchPlayerResultStatus()).isEqualTo(MatchPlayerResultStatus.DISQUALIFICATION);

        Optional<MatchPlayerInfo> ChikaPuka = matchInfoDto.getMatchPlayerInfoList().stream()
                .filter(matchPlayerInfo -> matchPlayerInfo.getGameId().equals("ChikaPuka"))
                .findFirst();

        assertThat(ChikaPuka.get().getScore()).isEqualTo(108);
        assertThat(ChikaPuka.get().getMatchPlayerResultStatus()).isEqualTo(MatchPlayerResultStatus.ADVANCE);
        assertThat(ChikaPuka.get().getPlayerStatus()).isEqualTo(PlayerStatus.WAITING);


        Optional<MatchPlayerInfo> 플레이어개장줄 = matchInfoDto.getMatchPlayerInfoList().stream()
                .filter(matchPlayerInfo -> matchPlayerInfo.getGameId().equals("플레이어개장줄"))
                .findFirst();
        Optional<MatchPlayerInfo> doaltlqkfrpdla = matchInfoDto.getMatchPlayerInfoList().stream()
                .filter(matchPlayerInfo -> matchPlayerInfo.getGameId().equals("doaltlqkfrpdla"))
                .findFirst();
        Optional<MatchPlayerInfo> Becrux = matchInfoDto.getMatchPlayerInfoList().stream()
                .filter(matchPlayerInfo -> matchPlayerInfo.getGameId().equals("Becrux"))
                .findFirst();

        assertThat(Becrux.get().getMatchPlayerResultStatus()).isEqualTo(MatchPlayerResultStatus.ADVANCE);
        assertThat(플레이어개장줄.get().getMatchPlayerResultStatus()).isEqualTo(MatchPlayerResultStatus.ADVANCE);
        assertThat(doaltlqkfrpdla.get().getMatchPlayerResultStatus()).isEqualTo(MatchPlayerResultStatus.DROPOUT);
        assertThat(matchInfoDto.getMatchPlayerInfoList().get(5).getGameId()).isEqualTo("반갑쨔나");

    }


    @Test
    @DisplayName("유효하지 않은 경기 - 가져온 결과에 모든 참가자가 있지 않음")
    void updateMatchPlayerScore_fail() {
        assertThatThrownBy(() -> matchPlayerService.updateMatchPlayerScore(1L, 3, 1695726000L))
                .isInstanceOf(MatchResultIdNotFoundException.class);
    }

    @Test
    @DisplayName("매치 플레이어 점수 업데이트 - 실패(이미 업데이트 되어있음.)")
    void updateMatchPlayerScore_Fail() {
        assertThatThrownBy(() -> matchPlayerService.updateMatchPlayerScore(1L, 2, 1691908000L))
                .isInstanceOf(MatchAlreadyUpdateException.class);
    }

    @Test
    @DisplayName("이전 경기 결과 조회 테스트")
    void getGameResult() {
        List<GameResultDto> gameResultList = matchPlayerService.getGameResult(1L);

        assertThat(gameResultList.size()).isEqualTo(2);
        assertThat(gameResultList.get(0).getMatchRankResultDtos().stream()
                .filter(matchRankResultDto -> matchRankResultDto.getGameId().equals("무진스 협회장"))
                .findFirst().get().getPlacement()).isEqualTo(6);

        assertThat(gameResultList.get(1).getMatchRankResultDtos().stream()
                .filter(matchRankResultDto -> matchRankResultDto.getGameId().equals("무진스 협회장"))
                .findFirst().get().getPlacement()).isEqualTo(2);
    }

    @Test
    @DisplayName("이전 경기 결과 조회 테스트-결과없음")
    void getGameResult_fail() {
        assertThatThrownBy(() -> matchPlayerService.getGameResult(2L)).isInstanceOf(MatchResultIdNotFoundException.class);
    }

}