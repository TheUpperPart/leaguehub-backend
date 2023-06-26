package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.MatchRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRankRepository extends JpaRepository <MatchRank, Long> {

    List<MatchRank> findByMatchResult_Id(Long id);
}
