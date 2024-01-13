package leaguehub.leaguehubbackend.domain.match.dto;

import lombok.Data;

@Data
public class MatchCallAdminDto {

    Integer matchRound;

    String matchName;

    String callName;
}
