package leaguehub.leaguehubbackend.repository.match;

import leaguehub.leaguehubbackend.entity.match.GameResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameResultRepository extends MongoRepository<GameResult, Long> {

}
