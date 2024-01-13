package leaguehub.leaguehubbackend.domain.match.exception.chat.exception;

import leaguehub.leaguehubbackend.domain.match.exception.chat.ChatExceptionCode;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.match.exception.chat.ChatExceptionCode.MATCH_CHAT_CONVERSION_EXCEPTION;

public class MatchChatMessageConversionException  extends RuntimeException{

    private final ChatExceptionCode exceptionCode;

    public MatchChatMessageConversionException() {
        super(MATCH_CHAT_CONVERSION_EXCEPTION.getMessage());
        this.exceptionCode = MATCH_CHAT_CONVERSION_EXCEPTION;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}

