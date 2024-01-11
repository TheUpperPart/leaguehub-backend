package leaguehub.leaguehubbackend.domain.channel.exception;

import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelStatusAlreadyException;
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
public class ChannelExceptionHandler {

    @ExceptionHandler(ChannelRequestException.class)
    public ResponseEntity<ExceptionResponse> channelCreateException(
            ChannelRequestException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ChannelBoardNotFoundException.class)
    public ResponseEntity<ExceptionResponse> channelBoardNotfoundException(
            ChannelBoardNotFoundException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<ExceptionResponse> channelNotFoundException(
            ChannelNotFoundException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

    @ExceptionHandler(ChannelStatusAlreadyException.class)
    public ResponseEntity<ExceptionResponse> channelStatusAlreadyException(
            ChannelStatusAlreadyException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

}
