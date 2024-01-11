package leaguehub.leaguehubbackend.domain.email.exception.exception;

import leaguehub.leaguehubbackend.domain.email.exception.EmailExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.domain.email.exception.EmailExceptionCode.UNAUTHORIZED_EMAIL_EXCEPTION;


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
