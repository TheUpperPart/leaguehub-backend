package leaguehub.leaguehubbackend.domain.match.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.match.exception.MatchExceptionCode.MATCH_NOT_END;


public class MatchNotEndException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public MatchNotEndException(){
        super(MATCH_NOT_END.getMessage());
        this.exceptionCode = MATCH_NOT_END;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}