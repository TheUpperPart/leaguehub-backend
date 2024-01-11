package leaguehub.leaguehubbackend.domain.match.dto;

import lombok.Data;

import java.util.List;

@Data
public class MatchRoundListDto {

    private Integer liveRound;

    private List<Integer> roundList;
}
