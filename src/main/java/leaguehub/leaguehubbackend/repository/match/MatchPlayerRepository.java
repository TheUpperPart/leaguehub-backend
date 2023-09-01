package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    List<MatchPlayer> findAllByMatch_Id(Long matchId);


    List<MatchPlayer> findAllByMatch_MatchNameAndMatch_MatchRound(String matchName, Integer matchRound);

    @Query("select mp from MatchPlayer mp join fetch mp.participant where mp.match.id = :matchId")
    List<MatchPlayer> findAllByMatch_IdOrderByPlayerScoreDesc(@Param("matchId") Long matchId);

    @Query("select mp from MatchPlayer mp join fetch mp.participant join fetch mp.match where mp.match.id =: matchId")
    List<MatchPlayer> findMatchPlayersAndMatchAndParticipantByMatchId(@Param("matchId") Long matchId);

}