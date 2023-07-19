package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.INVALID_PARTICIPATED_ROLE_REQUEST;

public class ParticipantInvalidRoleException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantInvalidRoleException(){
        super(INVALID_PARTICIPATED_ROLE_REQUEST.getMessage());
        this.exceptionCode = INVALID_PARTICIPATED_ROLE_REQUEST;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
