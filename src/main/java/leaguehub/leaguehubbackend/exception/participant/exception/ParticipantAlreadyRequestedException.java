package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.PARTICIPANT_ALREADY_REQUESTED;

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
