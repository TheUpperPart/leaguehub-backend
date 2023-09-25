package leaguehub.leaguehubbackend.mongo_repository;

import leaguehub.leaguehubbackend.entity.match.GameResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GameResultRepository extends MongoRepository<GameResult, Long> {
    Optional<GameResult> findGameResultById(Long id);
}
