package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByMatchLink(String matchLink);

    List<Match> findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(String channelLink, Integer matchRound);

    @Query("select ma from Match ma join fetch MatchPlayer join fetch MatchRank where ma.id =: matchId")
    Optional<Match> findMatchAndMatchPlayerAndMatchRank(@Param("matchId") Long matchId);
}
