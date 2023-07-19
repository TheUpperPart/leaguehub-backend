package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.INVALID_PARTICIPATED_PLAY_COUNT_REQUEST;

public class ParticipantInvalidPlayCountException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantInvalidPlayCountException(){
        super(INVALID_PARTICIPATED_PLAY_COUNT_REQUEST.getMessage());
        this.exceptionCode = INVALID_PARTICIPATED_PLAY_COUNT_REQUEST;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}