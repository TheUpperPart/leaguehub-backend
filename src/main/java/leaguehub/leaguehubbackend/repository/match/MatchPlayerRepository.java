package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    List<MatchPlayer> findAllByMatch_MatchLinkAndMatch_MatchName(String matchLink, String matchName);

    List<MatchPlayer> findAllByMatch_MatchNameAndMatch_MatchRound(String matchName, Integer matchRound);

    List<MatchPlayer> findAllByMatch_Id(Long matchId);
}