package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.PARTICIPANT_DUPLICATED_GAME_ID;


public class ParticipantDuplicatedGameIdException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public ParticipantDuplicatedGameIdException(){
        super(PARTICIPANT_DUPLICATED_GAME_ID.getMessage());
        this.exceptionCode = PARTICIPANT_DUPLICATED_GAME_ID;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
