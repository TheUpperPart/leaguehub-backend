package leaguehub.leaguehubbackend.dto.participant;

import lombok.Builder;
import lombok.Data;

@Data
public class ParticipantIdResponseDto {

    private Long matchPlayerId;

    //체크인: 1, 실격: 2
    private int matchPlayerStatus;

    @Builder
    public ParticipantIdResponseDto(Long matchPlayerId, int matchPlayerStatus){
        this.matchPlayerId = matchPlayerId;
        this.matchPlayerStatus = matchPlayerStatus;

    }
}
