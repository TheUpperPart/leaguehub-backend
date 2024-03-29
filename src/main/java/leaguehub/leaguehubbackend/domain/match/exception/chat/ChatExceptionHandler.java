package leaguehub.leaguehubbackend.domain.match.exception.chat;

import leaguehub.leaguehubbackend.domain.match.exception.chat.exception.MatchChatMessageConversionException;
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
public class ChatExceptionHandler {

    @ExceptionHandler(MatchChatMessageConversionException.class)
    public ResponseEntity<ExceptionResponse> invalidEmailAddress(
            MatchChatMessageConversionException e
    ) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.error("{}", exceptionCode.getMessage());

        return new ResponseEntity<>(
                new ExceptionResponse(exceptionCode),
                exceptionCode.getHttpStatus()
        );
    }

}
