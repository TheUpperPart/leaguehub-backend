package leaguehub.leaguehubbackend.exception.channel.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.channel.ChannelExceptionCode.*;

public class ChannelCreateException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public ChannelCreateException() {
        super(INVALID_CREATE_CHANNEL.getMessage());
        this.exceptionCode = INVALID_CREATE_CHANNEL;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
