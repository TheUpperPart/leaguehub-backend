package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.participant.ParticipantDto;

public class ParticipantFixture {

    public static ParticipantDto createParticipantResponseDto(String channelLink, String nickname){
        ParticipantDto participantResponseDto = new ParticipantDto();
        participantResponseDto.setChannelLink(channelLink);
        participantResponseDto.setGameId(nickname);

        return participantResponseDto;
    }
}
