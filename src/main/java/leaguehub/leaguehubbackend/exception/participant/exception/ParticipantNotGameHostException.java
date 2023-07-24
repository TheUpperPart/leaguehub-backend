package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.PARTICIPANT_NOT_GAME_HOST;

public class ParticipantNotGameHostException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public ParticipantNotGameHostException(){
        super(PARTICIPANT_NOT_GAME_HOST.getMessage());
        this.exceptionCode = PARTICIPANT_NOT_GAME_HOST;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
