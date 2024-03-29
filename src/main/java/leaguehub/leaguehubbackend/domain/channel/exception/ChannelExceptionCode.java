package leaguehub.leaguehubbackend.domain.channel.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum ChannelExceptionCode implements ExceptionCode {

    INVALID_PARTICIPATED_REQUEST(BAD_REQUEST, "CH-C-001", "유효하지 않은 대회 참가 요청입니다."),
    INELIGIBLE_PARTICIPANT_REQUEST(BAD_REQUEST, "CH-C-002", "정해진 대회 룰에 적합하지 않는 대회 참가 요청입니다."),
    INVALID_JOIN_REQUEST(BAD_REQUEST, "CH-C-003", "유효하지 않은 참가 링크입니다."),
    INVALID_CHANNEL_IMAGE(BAD_REQUEST, "CH-C-004", "유효하지 않은 이미지입니다."),
    INVALID_ACCESS_CODE(BAD_REQUEST, "CH-C-005", "유효하지 않은 대회 참가 코드입니다."),
    INVALID_REQUEST_CHANNEL(BAD_REQUEST, "CH-C-006", " 유효하지 않은 요청 값입니다."),
    CHANNEL_NOT_FOUND(NOT_FOUND, "CH-C-007", "채널을 찾을 수 없습니다."),
    CHANNEL_BOARD_NOT_FOUND(NOT_FOUND, "CH-C-008", "채널 게시판을 찾을 수 없습니다."),
    CHANNEL_STATUS_ALREADY_PROCEEDING(BAD_REQUEST, "CH-C-009", "해당 채널은 이미 경기 진행중입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
