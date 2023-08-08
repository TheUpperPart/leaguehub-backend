package leaguehub.leaguehubbackend.exception.channel.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.channel.ChannelExceptionCode.*;

public class ChannelRequestException extends IllegalArgumentException {

    private final ExceptionCode exceptionCode;

    public ChannelRequestException() {
        super(INVALID_REQUEST_CHANNEL.getMessage());
        this.exceptionCode = INVALID_REQUEST_CHANNEL;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
