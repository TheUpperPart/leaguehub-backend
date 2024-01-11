package leaguehub.leaguehubbackend.domain.match.exception.chat;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum ChatExceptionCode implements ExceptionCode {

    MATCH_CHAT_CONVERSION_EXCEPTION(INTERNAL_SERVER_ERROR, "CH-C-001", "메시지 변환 중 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
