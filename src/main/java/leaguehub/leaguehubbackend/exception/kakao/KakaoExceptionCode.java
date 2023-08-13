package leaguehub.leaguehubbackend.exception.kakao;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum KakaoExceptionCode implements ExceptionCode {
    INVALID_KAKAO_CODE(BAD_REQUEST, "KA-C-001", "유효하지 않은 카카오 코드입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
