package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.PARTICIPANT_GAME_ID_NOT_FOUND;


public class ParticipantGameIdNotFoundException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public ParticipantGameIdNotFoundException(){
        super(PARTICIPANT_GAME_ID_NOT_FOUND.getMessage());
        this.exceptionCode = PARTICIPANT_GAME_ID_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
