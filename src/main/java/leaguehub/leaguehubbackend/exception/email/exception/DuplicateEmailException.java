package leaguehub.leaguehubbackend.exception.email.exception;

import leaguehub.leaguehubbackend.exception.email.EmailExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.email.EmailExceptionCode.DUPLICATE_EMAIL_EXCEPTION;


public class DuplicateEmailException extends RuntimeException{

    private final EmailExceptionCode exceptionCode;

    public DuplicateEmailException() {
        super(DUPLICATE_EMAIL_EXCEPTION.getMessage());
        this.exceptionCode = DUPLICATE_EMAIL_EXCEPTION;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
