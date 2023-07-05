package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.INVALID_PARTICIPATED_ROLE_REQUEST;

public class ParticipatedInvalidRoleException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipatedInvalidRoleException(){
        super(INVALID_PARTICIPATED_ROLE_REQUEST.getMessage());
        this.exceptionCode = INVALID_PARTICIPATED_ROLE_REQUEST;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
