package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;

public class MatchFixture {



    public static MatchResponseDto createMatchResponseDto(String matchLink){
        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchLink(matchLink);
        matchResponseDto.setNickName("서초임");

        return matchResponseDto;
    }

    public static MatchResponseDto createFailResponseDto(String matchLink){
        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchLink(matchLink);
        matchResponseDto.setNickName("savokscmo");

        return matchResponseDto;
    }
}
