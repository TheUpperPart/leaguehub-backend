package leaguehub.leaguehubbackend.exception.global.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.GlobalErrorCode;

import static leaguehub.leaguehubbackend.exception.global.GlobalErrorCode.SERVER_ERROR;

public class GlobalServerErrorException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public GlobalServerErrorException(){
        super(SERVER_ERROR.getMessage());
        this.exceptionCode = SERVER_ERROR;
    }

    public ExceptionCode getExceptionCode(){
        return exceptionCode;
    }
}
