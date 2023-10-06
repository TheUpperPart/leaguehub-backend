package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchRank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRankRepository extends JpaRepository<MatchRank, Long> {
}