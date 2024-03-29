package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.PARTICIPANT_REAL_PLAYER_IS_MAX;

public class ParticipantRealPlayerIsMaxException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantRealPlayerIsMaxException(){
        super(PARTICIPANT_REAL_PLAYER_IS_MAX.getMessage());
        this.exceptionCode = PARTICIPANT_REAL_PLAYER_IS_MAX;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
