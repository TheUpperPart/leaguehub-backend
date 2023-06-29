package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchResult;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.match.MatchResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MatchResultService {

    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;

    public MatchResult saveMatchResult(String name, String passwd, String matchId){
        Match match = matchRepository.findByMatchPasswd(passwd);
        MatchResult matchResult = MatchResult.createMatchResult(matchId, match);
        matchResultRepository.save(matchResult);

        return matchResult;
    }
}
