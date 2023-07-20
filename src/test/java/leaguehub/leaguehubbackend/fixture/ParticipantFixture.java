package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import org.springframework.security.core.parameters.P;

public class ParticipantFixture {

    public static ParticipantResponseDto createParticipantResponseDto(String channelLink, String nickname){
        ParticipantResponseDto participantResponseDto = new ParticipantResponseDto();
        participantResponseDto.setChannelLink(channelLink);
        participantResponseDto.setNickname(nickname);

        return participantResponseDto;
    }
}
