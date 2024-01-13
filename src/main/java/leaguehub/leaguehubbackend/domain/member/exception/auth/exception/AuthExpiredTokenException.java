package leaguehub.leaguehubbackend.domain.member.exception.auth.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.domain.member.exception.auth.AuthExceptionCode.EXPIRED_TOKEN;

public class AuthExpiredTokenException extends AuthenticationException {
    private final ExceptionCode exceptionCode;
    public AuthExpiredTokenException() {
        super(EXPIRED_TOKEN.getCode());
        this.exceptionCode = EXPIRED_TOKEN;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
