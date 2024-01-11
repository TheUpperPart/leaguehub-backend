package leaguehub.leaguehubbackend.domain.match.repository;

import leaguehub.leaguehubbackend.domain.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(String channelLink, Integer matchRound);

    List<Match> findAllByChannel_ChannelLink(String channelLink);

    List<Match> findAllByChannel_ChannelLinkOrderByMatchRoundDesc(String channelLink);

    Optional<Match> findById(Long matchId);
}
