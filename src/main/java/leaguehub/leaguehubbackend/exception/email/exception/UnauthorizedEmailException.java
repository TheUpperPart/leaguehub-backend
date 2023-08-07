package leaguehub.leaguehubbackend.exception.email.exception;

import leaguehub.leaguehubbackend.exception.email.EmailExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.exception.email.EmailExceptionCode.UNAUTHORIZED_EMAIL_EXCEPTION;


public class UnauthorizedEmailException extends AuthenticationException {
    private final EmailExceptionCode exceptionCode;

    public UnauthorizedEmailException() {

        super(UNAUTHORIZED_EMAIL_EXCEPTION.getMessage());
        this.exceptionCode = UNAUTHORIZED_EMAIL_EXCEPTION;

    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
