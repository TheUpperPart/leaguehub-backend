package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchStatus;

import java.util.UUID;

public class MatchFixture {

    public static Match createMatch(){
        Match match = Match.builder()
                .matchStatus(MatchStatus.READY)
                .matchRound(1)
                .matchName("가나다")
                .matchPasswd("abc123456789").build();

        return match;
    }

    public static MatchResponseDto createMatchResponseDto(){
        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchName("가나다");
        matchResponseDto.setMatchPasswd("abc123456789");
        matchResponseDto.setNickName("서초임");

        return matchResponseDto;
    }

    public static MatchResponseDto createFailResponseDto(){
        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchName("가나다");
        matchResponseDto.setMatchPasswd("abc123456789");
        matchResponseDto.setNickName("savokscmo");

        return matchResponseDto;
    }
}
