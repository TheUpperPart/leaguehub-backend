package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {
    List<MatchPlayer> findAllByMatch_Id(Long matchId);

    @Query("select mp from MatchPlayer mp join fetch Participant join fetch Match where mp.match.id =: matchId")
    List<MatchPlayer> findMatchPlayersAndMatchAndParticipantByMatchId(@Param("matchId") Long matchId);
}