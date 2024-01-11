package leaguehub.leaguehubbackend.domain.participant.dto;

import lombok.Data;

@Data
public class ResponseUserGameInfoDto {

    private String tier;

    private Integer playCount;
}
