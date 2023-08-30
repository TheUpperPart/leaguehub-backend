package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;

public class MatchFixture {



    public static MatchResponseDto createMatchResponseDto(Long matchId){
        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchId(matchId);
        matchResponseDto.setGameId("서초임");

        return matchResponseDto;
    }

    public static MatchResponseDto createFailResponseDto(Long matchId){
        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchId(matchId);
        matchResponseDto.setGameId("savokscmo");

        return matchResponseDto;
    }
}
