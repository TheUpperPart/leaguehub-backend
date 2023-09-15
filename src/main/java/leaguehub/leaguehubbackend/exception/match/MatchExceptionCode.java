package leaguehub.leaguehubbackend.exception.match;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum MatchExceptionCode implements ExceptionCode {

    MATCH_NOT_FOUND(NOT_FOUND, "MA-C-001", "유효하지 않은 경기입니다."),
    MATCH_RESULT_NOT_FOUNT(NOT_FOUND, "MA-C-002", "매치 결과를 찾을 수 없습니다."),
    MATCH_NOT_ENOUGH_PLAYER(BAD_REQUEST, "MA-C-003", "매치 인원수가 충분하지 않습니다."),
    MATCH_ALREADY_UPDATE(BAD_REQUEST, "MA-C-004", "이미 매치의 점수가 업데이트 되었습니다."),
    MATCH_PLAYER_NOT_FOUND(NOT_FOUND, "MA-C-005", "해당 매치 플레이어가 없습니다."),
    MATCH_NOT_END(BAD_REQUEST, "MA-C-006", "이전 라운드 매치가 끝나지 않았습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
