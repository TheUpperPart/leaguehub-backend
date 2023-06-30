package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.PARTICIPANT_GAME_ID_NOT_FOUND;


public class ParticipantGameIdNotFoundException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public ParticipantGameIdNotFoundException(){
        super(PARTICIPANT_GAME_ID_NOT_FOUND.getMessage());
        this.exceptionCode = ParticipantExceptionCode.PARTICIPANT_GAME_ID_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
