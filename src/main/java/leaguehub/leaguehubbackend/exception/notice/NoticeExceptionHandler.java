package leaguehub.leaguehubbackend.exception.notice;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.exception.notice.exception.NoticeUnsupportedException;
import leaguehub.leaguehubbackend.exception.notice.exception.WebScrapingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class NoticeExceptionHandler {

    @ExceptionHandler(NoticeUnsupportedException.class)
    public ResponseEntity<ExceptionResponse> noticeUnsupportedException(
            NoticeUnsupportedException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(WebScrapingException.class)
    public ResponseEntity<ExceptionResponse> webScrapingException(
            WebScrapingException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }


}
