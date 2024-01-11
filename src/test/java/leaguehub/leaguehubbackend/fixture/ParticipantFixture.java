package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantDto;

public class ParticipantFixture {

    public static ParticipantDto createParticipantResponseDto(String nickname){
        ParticipantDto participantResponseDto = new ParticipantDto();
        participantResponseDto.setGameId(nickname);
        participantResponseDto.setNickname(nickname);

        return participantResponseDto;
    }
}
