package leaguehub.leaguehubbackend.dto.participant;

import lombok.Data;

@Data
public class ResponseUserGameInfoDto {

    private String tier;

    private String grade;

    private Integer playCount;
}
