package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.PARTICIPANT_ALREADY_REQUESTED;

public class ParticipantAlreadyRequestedException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantAlreadyRequestedException(){
        super(PARTICIPANT_ALREADY_REQUESTED.getMessage());
        this.exceptionCode = PARTICIPANT_ALREADY_REQUESTED;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
