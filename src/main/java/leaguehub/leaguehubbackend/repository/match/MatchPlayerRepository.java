package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    List<MatchPlayer> findAllByMatch_Id(Long matchId);

    List<MatchPlayer> findAllByMatch_MatchNameAndMatch_MatchRound(String matchName, Integer matchRound);

    @Query("select mp from MatchPlayer mp join fetch mp.participant where mp.match.id = :matchId")
    List<MatchPlayer> findAllByMatch_IdOrderByPlayerScoreDesc(@Param("matchId") Long matchId);

    @Query("select mp from MatchPlayer mp join fetch mp.participant join fetch mp.match where mp.match.id = :matchId " +
            "order by mp.playerScore desc, mp.participant.gameId")
    List<MatchPlayer> findMatchPlayersAndMatchAndParticipantByMatchId(@Param("matchId") Long matchId);

    @Query("select mp from MatchPlayer mp join fetch mp.participant join fetch mp.match where mp.match.id = :matchId " +
            "and mp.matchPlayerResultStatus != leaguehub.leaguehubbackend.entity.match.MatchPlayerResultStatus.DISQUALIFICATION " +
            "order by mp.playerScore desc, mp.participant.gameId")
    List<MatchPlayer> findMatchPlayersWithoutDisqualification(@Param("matchId") Long matchId);

    Optional<MatchPlayer> findByParticipantIdAndMatchId(Long participantId, Long matchId);

}