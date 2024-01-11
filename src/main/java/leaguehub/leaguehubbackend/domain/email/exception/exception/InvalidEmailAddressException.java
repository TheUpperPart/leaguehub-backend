package leaguehub.leaguehubbackend.domain.email.exception.exception;

import leaguehub.leaguehubbackend.domain.email.exception.EmailExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.email.exception.EmailExceptionCode.INVALID_EMAIL_ADDRESS;


public class InvalidEmailAddressException extends IllegalArgumentException  {
    private final EmailExceptionCode exceptionCode;

    public InvalidEmailAddressException() {

        super(INVALID_EMAIL_ADDRESS.getMessage());
        this.exceptionCode = INVALID_EMAIL_ADDRESS;

    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
