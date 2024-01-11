package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.INVALID_PARTICIPANT_PLAY_COUNT_REQUEST;

public class ParticipantInvalidPlayCountException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantInvalidPlayCountException(){
        super(INVALID_PARTICIPANT_PLAY_COUNT_REQUEST.getMessage());
        this.exceptionCode = INVALID_PARTICIPANT_PLAY_COUNT_REQUEST;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
