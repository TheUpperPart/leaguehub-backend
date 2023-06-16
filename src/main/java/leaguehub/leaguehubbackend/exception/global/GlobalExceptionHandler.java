package leaguehub.leaguehubbackend.exception.global;

import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.kakao.exception.KakaoInvalidCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalServerErrorException.class)
    public ResponseEntity<ExceptionResponse> internalServerErrorException(
            KakaoInvalidCodeException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }
}
