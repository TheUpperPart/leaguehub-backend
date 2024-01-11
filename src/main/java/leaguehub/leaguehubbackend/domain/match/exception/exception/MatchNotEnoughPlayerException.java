package leaguehub.leaguehubbackend.domain.match.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.match.exception.MatchExceptionCode.MATCH_NOT_ENOUGH_PLAYER;

public class MatchNotEnoughPlayerException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public MatchNotEnoughPlayerException(){
        super(MATCH_NOT_ENOUGH_PLAYER.getMessage());
        this.exceptionCode = MATCH_NOT_ENOUGH_PLAYER;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
