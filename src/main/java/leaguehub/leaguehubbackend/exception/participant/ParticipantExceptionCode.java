package leaguehub.leaguehubbackend.exception.participant;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum ParticipantExceptionCode implements ExceptionCode {

    INVALID_PARTICIPANT_IMAGE(BAD_REQUEST, "PA-C-003", "유효하지 않은 이미지입니다."),
    PARTICIPANT_GAME_ID_NOT_FOUND(NOT_FOUND, "PA-C-004", "게임 ID를 찾을 수 없습니다."),
    INVALID_PARTICIPANT_AUTH(UNAUTHORIZED, "PA-C-005", "해당 채널의 권한이 유효하지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
