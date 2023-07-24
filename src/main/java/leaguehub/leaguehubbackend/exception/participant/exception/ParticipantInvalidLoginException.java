package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.INVALID_PARTICIPANT_LOGIN_REQUEST;

public class ParticipantInvalidLoginException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantInvalidLoginException(){
        super(INVALID_PARTICIPANT_LOGIN_REQUEST.getMessage());
        this.exceptionCode = INVALID_PARTICIPANT_LOGIN_REQUEST;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
