package leaguehub.leaguehubbackend.domain.member.exception.auth.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.domain.member.exception.auth.AuthExceptionCode.REQUEST_TOKEN_NOT_FOUND;

public class AuthTokenNotFoundException extends AuthenticationException {
    private final ExceptionCode exceptionCode;
    public AuthTokenNotFoundException() {
        super(REQUEST_TOKEN_NOT_FOUND.getCode());
        this.exceptionCode = REQUEST_TOKEN_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

}

