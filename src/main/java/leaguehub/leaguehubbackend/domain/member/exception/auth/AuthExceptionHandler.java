package leaguehub.leaguehubbackend.domain.member.exception.auth;

import leaguehub.leaguehubbackend.domain.member.exception.auth.exception.AuthExpiredTokenException;
import leaguehub.leaguehubbackend.domain.member.exception.auth.exception.AuthInvalidRefreshToken;
import leaguehub.leaguehubbackend.domain.member.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.domain.member.exception.auth.exception.AuthTokenNotFoundException;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    @ExceptionHandler(AuthInvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> authInvalidTokenException(
            AuthInvalidTokenException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(AuthExpiredTokenException.class)
    public ResponseEntity<ExceptionResponse> authExpiredTokenException(
            AuthExpiredTokenException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(AuthInvalidRefreshToken.class)
    public ResponseEntity<ExceptionResponse> authInvalidRefreshToken(
            AuthInvalidRefreshToken e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(AuthTokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> authNoRefreshToken(
            AuthTokenNotFoundException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

}
