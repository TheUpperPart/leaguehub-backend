package leaguehub.leaguehubbackend.domain.match.dto;

import lombok.Data;

import java.util.List;

@Data
public class RiotAPIDto {

    private String matchUuid;

    private List<MatchRankResultDto> matchRankResultDtoList;

    public RiotAPIDto(String matchUuid, List<MatchRankResultDto> matchRankResultDtoList) {
        this.matchUuid = matchUuid;
        this.matchRankResultDtoList = matchRankResultDtoList;
    }
}
