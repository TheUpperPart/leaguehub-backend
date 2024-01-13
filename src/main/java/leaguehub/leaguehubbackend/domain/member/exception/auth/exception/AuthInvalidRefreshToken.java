package leaguehub.leaguehubbackend.domain.member.exception.auth.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.domain.member.exception.auth.AuthExceptionCode.INVALID_REFRESH_TOKEN;


public class AuthInvalidRefreshToken extends AuthenticationException {
    private final ExceptionCode exceptionCode;
    public AuthInvalidRefreshToken() {
        super(INVALID_REFRESH_TOKEN.getCode());
        this.exceptionCode = INVALID_REFRESH_TOKEN;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
