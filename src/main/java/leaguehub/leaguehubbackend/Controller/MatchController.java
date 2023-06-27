package leaguehub.leaguehubbackend.Controller;

import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.service.match.MatchRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MatchController {

    private final MatchRankService matchRankService;

    @PostMapping("/match/matchResult")
    public ResponseEntity createMatchRank(@RequestBody MatchResponseDto matchResponseDto){

        matchRankService.setMatchRank(matchResponseDto);

        return new ResponseEntity<>("매치 결과가 생성되었습니다.", HttpStatus.CREATED);
    }

    @GetMapping("/match/matchRank")
    public List<MatchRankResultDto> matchRankResult(String matchId){

        return matchRankService.getMatchDetail(matchId);
    }
}
