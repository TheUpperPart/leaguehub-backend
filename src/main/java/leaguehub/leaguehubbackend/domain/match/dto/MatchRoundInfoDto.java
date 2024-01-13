package leaguehub.leaguehubbackend.domain.match.dto;

import lombok.Data;

import java.util.List;

@Data
public class MatchRoundInfoDto {

    private String myGameId;

    private List<MatchInfoDto> matchInfoDtoList;
}
