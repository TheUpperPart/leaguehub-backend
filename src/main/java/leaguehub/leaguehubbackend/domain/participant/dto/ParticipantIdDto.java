package leaguehub.leaguehubbackend.domain.participant.dto;


import lombok.Data;

@Data
public class ParticipantIdDto {

    private String accessToken;

    private Long participantId;

    private Long matchPlayerId;

    //관리자: 0, 플레이어: 1, 관전자: 2
    private int role;
}
