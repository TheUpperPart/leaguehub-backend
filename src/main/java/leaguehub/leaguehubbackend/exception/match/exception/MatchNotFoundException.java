package leaguehub.leaguehubbackend.exception.match.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.match.MatchExceptionCode.MATCH_NOT_FOUND;

public class MatchNotFoundException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public MatchNotFoundException(){
        super(MATCH_NOT_FOUND.getMessage());
        this.exceptionCode = MATCH_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
