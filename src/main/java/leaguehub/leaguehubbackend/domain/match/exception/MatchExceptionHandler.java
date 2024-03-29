package leaguehub.leaguehubbackend.domain.match.exception;

import leaguehub.leaguehubbackend.domain.match.exception.exception.*;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
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

    @ExceptionHandler(MatchNotEnoughPlayerException.class)
    public ResponseEntity<ExceptionResponse> matchNotEnoughPlayerException(
            MatchNotEnoughPlayerException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(MatchAlreadyUpdateException.class)
    public ResponseEntity<ExceptionResponse> matchAlreadyUpdateException(
            MatchAlreadyUpdateException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(MatchNotEndException.class)
    public ResponseEntity<ExceptionResponse> MatchNotEndException(
            MatchNotEndException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }
}
