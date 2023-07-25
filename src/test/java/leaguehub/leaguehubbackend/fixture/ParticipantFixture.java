package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;

public class ParticipantFixture {

    public static ParticipantResponseDto createParticipantResponseDto(String channelLink, String nickname){
        ParticipantResponseDto participantResponseDto = new ParticipantResponseDto();
        participantResponseDto.setChannelLink(channelLink);
        participantResponseDto.setGameId(nickname);

        return participantResponseDto;
    }
}
