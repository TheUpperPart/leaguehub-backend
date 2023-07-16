package leaguehub.leaguehubbackend.exception.auth.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode.INVALID_TOKEN;

public class AuthInvalidTokenException extends AuthenticationException {
    private final ExceptionCode exceptionCode;
    public AuthInvalidTokenException() {
        super(INVALID_TOKEN.getCode());
        this.exceptionCode = INVALID_TOKEN;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

}
