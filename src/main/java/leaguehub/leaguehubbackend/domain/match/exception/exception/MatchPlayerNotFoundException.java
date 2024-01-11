package leaguehub.leaguehubbackend.domain.match.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.match.exception.MatchExceptionCode.MATCH_PLAYER_NOT_FOUND;


public class MatchPlayerNotFoundException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public MatchPlayerNotFoundException(){
        super(MATCH_PLAYER_NOT_FOUND.getMessage());
        this.exceptionCode = MATCH_PLAYER_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}