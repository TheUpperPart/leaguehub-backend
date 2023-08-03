package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchResult;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.match.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MatchResultService {

    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;

    //uuid로 찾아서 저장한다.
    public MatchResult saveMatchResult(String matchLink, String matchCode) {
        Match match = matchRepository.findByMatchLink(matchLink)
                .orElseThrow(MatchNotFoundException::new);
        MatchResult matchResult = MatchResult.createMatchResult(matchCode, match);
        matchResultRepository.save(matchResult);

        return matchResult;
    }
}
