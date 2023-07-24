package leaguehub.leaguehubbackend.exception.member;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

    MEMBER_NOT_FOUND(NOT_FOUND, "MB-C-001", "존재하지 않는 회원입니다."),
    INVALID_MEMBER_IMAGE(BAD_REQUEST, "MB-C-002", "유효하지 않은 이미지입니다."),
    INVALID_EMAIL_ADDRESS(BAD_REQUEST, "MB-C-003", "유효하지 않은 이메일 형식입니다."),
    DUPLICATE_EMAIL_EXCEPTION(CONFLICT, "MB-C-004", "중복되는 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
