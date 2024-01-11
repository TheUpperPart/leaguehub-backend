package leaguehub.leaguehubbackend.domain.match.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.match.exception.MatchExceptionCode.MATCH_NOT_FOUND;

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
