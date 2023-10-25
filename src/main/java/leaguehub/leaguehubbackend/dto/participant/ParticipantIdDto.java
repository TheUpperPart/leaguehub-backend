package leaguehub.leaguehubbackend.dto.participant;


import lombok.Data;

@Data
public class ParticipantIdDto {

    private String accessToken;

    private Long participantId;

    private Long matchPlayerId;
}
