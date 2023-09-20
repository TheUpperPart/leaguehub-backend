package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchSetRepository extends JpaRepository<MatchSet, Long> {
    Optional<MatchSet> findMatchSetByMatchIdAndAndSetCount(Long matchId, Integer setCount);

    List<MatchSet> findAllByMatch_Channel_ChannelLink(String channelLink);

    List<MatchSet> findMatchSetsByMatch_Id(Long matchId);

}