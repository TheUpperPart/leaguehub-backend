package leaguehub.leaguehubbackend.domain.participant.dto;

import lombok.Data;

@Data
public class ResponseStatusPlayerDto {

    private Long pk;

    private String nickname;

    private String imgSrc;

    private String gameId;

    private String tier;

}
