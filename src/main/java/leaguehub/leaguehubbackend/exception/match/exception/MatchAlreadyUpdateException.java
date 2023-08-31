package leaguehub.leaguehubbackend.exception.match.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.match.MatchExceptionCode.MATCH_ALREADY_UPDATE;

public class MatchAlreadyUpdateException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public MatchAlreadyUpdateException() {
        super(MATCH_ALREADY_UPDATE.getMessage());
        this.exceptionCode = MATCH_ALREADY_UPDATE;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
