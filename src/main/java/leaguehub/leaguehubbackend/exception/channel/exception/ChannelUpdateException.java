package leaguehub.leaguehubbackend.exception.channel.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.channel.ChannelExceptionCode.INVALID_CHANNEL_UPDATE;

public class ChannelUpdateException extends IllegalArgumentException {
    private final ExceptionCode exceptionCode;

    public ChannelUpdateException() {
        super(INVALID_CHANNEL_UPDATE.getMessage());
        this.exceptionCode = INVALID_CHANNEL_UPDATE;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
