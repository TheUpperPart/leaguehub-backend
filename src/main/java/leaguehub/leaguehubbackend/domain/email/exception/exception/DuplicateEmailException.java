package leaguehub.leaguehubbackend.domain.email.exception.exception;

import leaguehub.leaguehubbackend.domain.email.exception.EmailExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.email.exception.EmailExceptionCode.DUPLICATE_EMAIL_EXCEPTION;


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
