package leaguehub.leaguehubbackend.exception.participant;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.exception.participant.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ParticipantExceptionHandler {

    @ExceptionHandler(ParticipantGameIdNotFoundException.class)
    public ResponseEntity<ExceptionResponse> participantNotFoundException(
            ParticipantGameIdNotFoundException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(InvalidParticipantAuthException.class)
    public ResponseEntity<ExceptionResponse> InvalidParticipantAuthException(
            InvalidParticipantAuthException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantInvalidLoginException.class)
    public ResponseEntity<ExceptionResponse> ParticipantInvalidLoginException(
            ParticipantInvalidLoginException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantInvalidPlayCountException.class)
    public ResponseEntity<ExceptionResponse> ParticipantInvalidPlayCountException(
            ParticipantInvalidPlayCountException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantInvalidRankException.class)
    public ResponseEntity<ExceptionResponse> ParticipantInvalidRankException(
            ParticipantInvalidRankException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantInvalidRoleException.class)
    public ResponseEntity<ExceptionResponse> ParticipantInvalidRoleException(
            ParticipantInvalidRoleException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantRejectedRequestedException.class)
    public ResponseEntity<ExceptionResponse> ParticipantRejectedRequestedException(
            ParticipantRejectedRequestedException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }
    @ExceptionHandler(ParticipantAlreadyRequestedException.class)
    public ResponseEntity<ExceptionResponse> ParticipantAlreadyRequestedException(
            ParticipantAlreadyRequestedException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantDuplicatedGameIdException.class)
    public ResponseEntity<ExceptionResponse> ParticipantDuplicatedGameIdException(
            ParticipantDuplicatedGameIdException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantNotGameHostException.class)
    public ResponseEntity<ExceptionResponse> ParticipantNotGameHostException(
            ParticipantNotGameHostException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantRealPlayerIsMaxException.class)
    public ResponseEntity<ExceptionResponse> ParticipantRealPlayerIsMaxException(
            ParticipantRealPlayerIsMaxException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    public ResponseEntity<ExceptionResponse> ParticipantNotFoundException(
            ParticipantNotFoundException e
    ){
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }
}
