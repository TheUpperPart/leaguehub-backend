package leaguehub.leaguehubbackend.domain.match.repository;

import leaguehub.leaguehubbackend.domain.match.entity.MatchRank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRankRepository extends JpaRepository<MatchRank, Long> {
}