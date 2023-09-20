package leaguehub.leaguehubbackend.exception.channel;

import leaguehub.leaguehubbackend.exception.channel.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelStatusAlreadyException;
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
