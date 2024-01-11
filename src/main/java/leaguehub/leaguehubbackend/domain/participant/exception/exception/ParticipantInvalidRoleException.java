package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.INVALID_PARTICIPANT_ROLE_REQUEST;

public class ParticipantInvalidRoleException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantInvalidRoleException(){
        super(INVALID_PARTICIPANT_ROLE_REQUEST.getMessage());
        this.exceptionCode = INVALID_PARTICIPANT_ROLE_REQUEST;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
