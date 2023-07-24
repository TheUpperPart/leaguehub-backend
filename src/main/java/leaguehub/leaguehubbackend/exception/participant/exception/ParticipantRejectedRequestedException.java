package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.PARTICIPANT_REJECTED_REQUESTED;

public class ParticipantRejectedRequestedException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public ParticipantRejectedRequestedException(){
        super(PARTICIPANT_REJECTED_REQUESTED.getMessage());
        this.exceptionCode = PARTICIPANT_REJECTED_REQUESTED;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
