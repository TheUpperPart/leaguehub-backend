package leaguehub.leaguehubbackend.exception.auth.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode.AUTH_MEMBER_NOT_FOUND;

public class AuthMemberNotFoundException extends AuthenticationException {
    private final ExceptionCode exceptionCode;
    public AuthMemberNotFoundException() {
        super(AUTH_MEMBER_NOT_FOUND.getCode());
        this.exceptionCode = AUTH_MEMBER_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
