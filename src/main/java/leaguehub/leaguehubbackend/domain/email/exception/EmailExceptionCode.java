package leaguehub.leaguehubbackend.domain.email.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum EmailExceptionCode implements ExceptionCode {

    INVALID_EMAIL_ADDRESS(BAD_REQUEST, "MB-C-003", "유효하지 않은 이메일 형식입니다."),
    DUPLICATE_EMAIL_EXCEPTION(CONFLICT, "MB-C-004", "중복되는 이메일입니다."),
    UNAUTHORIZED_EMAIL_EXCEPTION(UNAUTHORIZED, "MB-C-005", "이메일이 인증되지 않은 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
