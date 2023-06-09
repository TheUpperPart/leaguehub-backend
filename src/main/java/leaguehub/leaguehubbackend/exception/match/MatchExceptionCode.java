package leaguehub.leaguehubbackend.exception.match;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum MatchExceptionCode implements ExceptionCode {

    MATCH_NOT_FOUND(NOT_FOUND, "MA-C-001", "유효하지 않은 경기입니다."),
    MATCH_RESULT_NOT_FOUNT(NOT_FOUND, "MA-C-002", "매치 결과를 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
