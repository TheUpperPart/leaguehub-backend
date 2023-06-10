package leaguehub.leaguehubbackend.exception.channel;

import leaguehub.leaguehubbackend.exception.channel.exception.ChannelCreateException;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@RequiredArgsConstructor
public class ChannelExceptionHandler {

    @ExceptionHandler(ChannelCreateException.class)
    public ResponseEntity<ExceptionResponse> memberNotFoundException(
            ChannelCreateException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }
}
