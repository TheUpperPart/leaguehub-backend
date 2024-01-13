package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.PARTICIPANT_NOT_FOUNT;

public class ParticipantNotFoundException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantNotFoundException(){
        super(PARTICIPANT_NOT_FOUNT.getMessage());
        this.exceptionCode = PARTICIPANT_NOT_FOUNT;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
