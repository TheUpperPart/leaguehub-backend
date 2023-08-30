package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    List<MatchPlayer> findAllByMatch_MatchLinkAndMatch_MatchName(String matchLink, String matchName);

    List<MatchPlayer> findAllByMatch_MatchNameAndMatch_MatchRound(String matchName, Integer matchRound);

    /**
     * 매치의 매치 플레이어와 그 매치와 관련된  MatchRank, Participant를 가져온다.
     * @param matchId
     * @return
     */
    @Query("select mp from MatchPlayer mp join fetch Participant p join fetch MatchRank where mp.match.id =: matchId")
    List<MatchPlayer> findAllByMatch_Id(@Param("matchId") Long matchId);
}