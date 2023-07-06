package leaguehub.leaguehubbackend.dto.participant;

import lombok.Data;

@Data
public class ResponseUserDetailDto {

    String tier;

    String grade;

    Integer playCount;
}
