package leaguehub.leaguehubbackend.domain.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ParticipantExceptionCode implements ExceptionCode {

    INVALID_PARTICIPANT_IMAGE(BAD_REQUEST, "PA-C-003", "유효하지 않은 이미지입니다."),
    PARTICIPANT_GAME_ID_NOT_FOUND(NOT_FOUND, "PA-C-004", "게임 ID를 찾을 수 없습니다."),
    INVALID_PARTICIPANT_LOGIN_REQUEST(BAD_REQUEST, "PA-C-005", "로그인이 필요합니다."),
    INVALID_PARTICIPANT_ROLE_REQUEST(BAD_REQUEST, "PA-C-006", "이미 참가하였거나 경기 관리자입니다."),
    INVALID_PARTICIPANT_TIER_REQUEST(BAD_REQUEST, "PA-C-007", "유저 티어가 설정된 티어보다 높습니다."),
    INVALID_PARTICIPANT_PLAY_COUNT_REQUEST(BAD_REQUEST, "PA-C-008", "경기 횟수가 설정된 횟수보다 낮습니다."),
    INVALID_PARTICIPANT_AUTH(UNAUTHORIZED, "PA-C-009", "해당 채널의 권한이 유효하지 않습니다"),
    PARTICIPANT_ALREADY_REQUESTED(BAD_REQUEST, "PA-C-010", "이미 참가요청 되었습니다."),
    PARTICIPANT_REJECTED_REQUESTED(BAD_REQUEST, "PA-C-011", "거절된 사용자입니다."),
    PARTICIPANT_DUPLICATED_GAME_ID(BAD_REQUEST, "PA-C-012", "해당 게임아이디는 이미 존재합니다."),
    PARTICIPANT_NOT_GAME_HOST(UNAUTHORIZED, "PA-C-013", "해당 채널 관리자가 아닙니다."),
    PARTICIPANT_REAL_PLAYER_IS_MAX(BAD_REQUEST, "PA-C-014", "플레이어의 수가 최대입니다."),
    PARTICIPANT_NOT_FOUNT(NOT_FOUND, "PA-C-015", "참가자를 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
