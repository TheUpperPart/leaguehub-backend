package leaguehub.leaguehubbackend.exception.participant;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum ParticipantExceptionCode implements ExceptionCode {

    INVALID_PARTICIPANT_IMAGE(BAD_REQUEST, "PA-C-003", "유효하지 않은 이미지입니다."),
    PARTICIPANT_GAME_ID_NOT_FOUND(NOT_FOUND, "PA-C-004", "게임 ID를 찾을 수 없습니다."),
    INVALID_PARTICIPATED_LOGIN_REQUEST(BAD_REQUEST, "PA-C-005", "로그인이 필요합니다."),
    INVALID_PARTICIPATED_ROLE_REQUEST(BAD_REQUEST, "PA-C-006", "이미 참가하였거나 경기 관리자입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
