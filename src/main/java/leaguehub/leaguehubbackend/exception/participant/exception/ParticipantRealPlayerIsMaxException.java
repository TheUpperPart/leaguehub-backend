package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.PARTICIPANT_REAL_PLAYER_IS_MAX;

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
