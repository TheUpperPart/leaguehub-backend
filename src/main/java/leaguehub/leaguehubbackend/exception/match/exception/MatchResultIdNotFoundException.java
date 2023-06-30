package leaguehub.leaguehubbackend.exception.match.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.match.MatchExceptionCode.MATCH_NOT_FOUND;
import static leaguehub.leaguehubbackend.exception.match.MatchExceptionCode.MATCH_RESULT_NOT_FOUNT;

public class MatchResultIdNotFoundException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public MatchResultIdNotFoundException(){
        super(MATCH_NOT_FOUND.getMessage());
        this.exceptionCode = MATCH_RESULT_NOT_FOUNT;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
