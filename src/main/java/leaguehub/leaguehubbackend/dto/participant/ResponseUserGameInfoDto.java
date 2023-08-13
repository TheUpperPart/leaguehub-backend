package leaguehub.leaguehubbackend.dto.participant;

import lombok.Data;

@Data
public class ResponseUserGameInfoDto {

    String tier;

    String grade;

    Integer playCount;
}
