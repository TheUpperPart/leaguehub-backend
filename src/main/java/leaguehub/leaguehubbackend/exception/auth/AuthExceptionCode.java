package leaguehub.leaguehubbackend.exception.auth;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {

    /**
     * JWT
     * 001 ~ 099
     */

    INVALID_TOKEN(UNAUTHORIZED, "AT-C-001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "AT-C-002", "만료된 토큰입니다."),
    NOT_EXPIRED_TOKEN(BAD_REQUEST, "AT-C-003", "만료되지 않은 토큰입니다."),
    REQUEST_TOKEN_NOT_FOUND(BAD_REQUEST, "AT-C-004", "요청에 토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "AT-C-005", "해당 리프레쉬 토큰을 가지는 멤버가 없습니다."),
    UNTRUSTED_CREDENTIAL(UNAUTHORIZED, "AT-C-006", "신뢰할 수 없는 자격증명 입니다."),
    LOGGED_OUT_TOKEN(UNAUTHORIZED, "AT-C-007", "로그아웃된 토큰입니다."),

    /**
     * MEMBER
     * 100 ~ 199
     */
    LOGIN_PROVIDER_MISMATCH(BAD_REQUEST, "AT-C-100", "잘못된 OAuth2 인증입니다."),
    INVALID_LOGIN_PROVIDER(BAD_REQUEST, "AT-C-101", "유효하지 않은 로그인 제공자입니다."),
    INVALID_MEMBER_ROLE(FORBIDDEN, "AT-C-102", "유효하지 않은 사용자 권한입니다."),
    NOT_AUTHORIZATION_USER(NOT_FOUND, "AT-C-103", "인가된 사용자가 아닙니다."),
    INVALID_REDIRECT_URI(UNAUTHORIZED, "AT-C-104", "허용되지 않은 리다이렉션 URI 입니다."),
    AUTH_MEMBER_NOT_FOUND(NOT_FOUND, "AT-C-105", "존재하지 않는 회원입니다."),


    /**
     * Common Exception
     * 200 ~
     */
    AUTHENTICATION_ERROR(UNAUTHORIZED, "AT-C-200", "Authentication exception."),
    INTERNAL_AUTHENTICATION_SERVICE_EXCEPTION(INTERNAL_SERVER_ERROR, "AT-S-200", "Internal authentication service exception.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
