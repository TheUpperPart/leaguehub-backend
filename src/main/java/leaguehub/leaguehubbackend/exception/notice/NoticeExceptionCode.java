package leaguehub.leaguehubbackend.exception.notice;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum NoticeExceptionCode implements ExceptionCode {

    NOTICE_UNSUPPORTED(BAD_REQUEST, "NT-C-002", "지원되지 않는 공지사항 기능입니다."),
    WEB_SCRAPING_ERROR(BAD_REQUEST, "WS-C-001", "웹 페이지에서 정보를 추출하는 과정에서 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
