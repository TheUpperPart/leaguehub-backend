package leaguehub.leaguehubbackend.dto.participant;

import lombok.Data;

@Data
public class ResponseStatusPlayerDto {

    Long pk;

    String nickname;

    String gameId;

    String tier;

}
