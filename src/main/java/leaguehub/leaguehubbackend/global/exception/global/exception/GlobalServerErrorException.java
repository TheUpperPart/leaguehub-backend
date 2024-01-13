package leaguehub.leaguehubbackend.global.exception.global.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.global.exception.global.GlobalErrorCode.SERVER_ERROR;

public class GlobalServerErrorException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public GlobalServerErrorException() {

        super(SERVER_ERROR.getMessage());
        this.exceptionCode = SERVER_ERROR;
    }

    public ExceptionCode getExceptionCode() {

        return exceptionCode;
    }
}
