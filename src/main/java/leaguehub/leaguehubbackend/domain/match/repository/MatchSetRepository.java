package leaguehub.leaguehubbackend.domain.match.repository;

import leaguehub.leaguehubbackend.domain.match.entity.MatchSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchSetRepository extends JpaRepository<MatchSet, Long> {
    Optional<MatchSet> findMatchSetByMatchIdAndAndSetCount(Long matchId, Integer setCount);

    List<MatchSet> findAllByMatch_Channel_ChannelLink(String channelLink);

    @Query("select distinct ms from MatchSet ms join fetch ms.matchRankList where ms.match.id = :matchId")
    List<MatchSet> findMatchSetsByMatch_Id(@Param("matchId") Long matchId);

}