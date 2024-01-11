package leaguehub.leaguehubbackend.global.exception.global;

import leaguehub.leaguehubbackend.domain.member.exception.kakao.exception.KakaoInvalidCodeException;
import leaguehub.leaguehubbackend.global.exception.global.exception.GlobalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity processValidationError(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        List<ObjectError> errors = bindingResult.getAllErrors();
        for (ObjectError error : errors) {
            log.error(error.getObjectName());
        }

        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity alreadyExistsValueInDataBase(
            DataIntegrityViolationException exception
    ) {
        log.error("{}", exception.getMessage());

        return new ResponseEntity<>(
                exception.getMessage(),
                BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity globalException(Exception e) {
        log.error("{}", e);

        return new ResponseEntity(e.getMessage(), INTERNAL_SERVER_ERROR);
    }
}
