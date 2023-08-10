package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.PARTICIPANT_NOT_FOUNT;

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
