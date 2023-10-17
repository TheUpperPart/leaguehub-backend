package leaguehub.leaguehubbackend.dto.match;

import lombok.Data;

@Data
public class MatchCallAdminDto {

    Integer matchRound;

    String matchName;

    String callName;
}
