package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByMatchLink(String matchLink);

    List<Match> findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(String channelLink, Integer matchRound);
}
