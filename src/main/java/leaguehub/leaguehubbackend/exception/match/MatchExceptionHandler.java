package leaguehub.leaguehubbackend.exception.match;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class MatchExceptionHandler {

    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<ExceptionResponse> matchNotFoundException(
            MatchNotFoundException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(MatchResultIdNotFoundException.class)
    public ResponseEntity<ExceptionResponse> matchResultIdNotFoundException(
            MatchResultIdNotFoundException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }
}
