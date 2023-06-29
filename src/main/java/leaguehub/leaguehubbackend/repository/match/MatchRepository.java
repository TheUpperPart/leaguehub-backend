package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Match findByMatchPasswd(String matchPasswd);

}
