package leaguehub.leaguehubbackend.domain.member.exception.auth.exception;

import leaguehub.leaguehubbackend.domain.member.exception.auth.AuthExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

public class AuthInvalidTokenException extends AuthenticationException {
    private final ExceptionCode exceptionCode;
    public AuthInvalidTokenException() {
        super(AuthExceptionCode.INVALID_TOKEN.getCode());
        this.exceptionCode = AuthExceptionCode.INVALID_TOKEN;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

}
