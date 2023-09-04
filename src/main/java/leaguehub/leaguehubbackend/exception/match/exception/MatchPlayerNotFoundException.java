package leaguehub.leaguehubbackend.exception.match.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.match.MatchExceptionCode.MATCH_PLAYER_NOT_FOUND;


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