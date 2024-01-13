package leaguehub.leaguehubbackend.domain.match.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.match.exception.MatchExceptionCode.MATCH_ALREADY_UPDATE;

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
