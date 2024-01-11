package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.PARTICIPANT_REJECTED_REQUESTED;

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
