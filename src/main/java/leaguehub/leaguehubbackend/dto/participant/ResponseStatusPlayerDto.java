package leaguehub.leaguehubbackend.dto.participant;

import lombok.Data;

@Data
public class ResponseStatusPlayerDto {

    private Long pk;

    private String nickname;

    private String imgSrc;

    private String gameId;

    private String tier;

}
