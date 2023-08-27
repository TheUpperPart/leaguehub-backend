package leaguehub.leaguehubbackend.dto.match;

import lombok.Data;

import java.util.List;

@Data
public class MatchRoundInfoDto {

    private String myGameId;

    private List<MatchInfoDto> matchInfoDtoList;
}
