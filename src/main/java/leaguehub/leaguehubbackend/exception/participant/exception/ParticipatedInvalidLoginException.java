package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.INVALID_PARTICIPATED_LOGIN_REQUEST;

public class ParticipatedInvalidLoginException extends RuntimeException{


    private final ExceptionCode exceptionCode;

    public ParticipatedInvalidLoginException(){
        super(INVALID_PARTICIPATED_LOGIN_REQUEST.getMessage());
        this.exceptionCode = INVALID_PARTICIPATED_LOGIN_REQUEST;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
