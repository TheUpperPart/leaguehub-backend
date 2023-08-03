package leaguehub.leaguehubbackend.exception.email.exception;

import leaguehub.leaguehubbackend.exception.email.EmailExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.email.EmailExceptionCode.INVALID_EMAIL_ADDRESS;


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
