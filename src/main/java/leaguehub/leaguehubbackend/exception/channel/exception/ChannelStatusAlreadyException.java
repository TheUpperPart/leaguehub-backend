package leaguehub.leaguehubbackend.exception.channel.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.channel.ChannelExceptionCode.CHANNEL_STATUS_ALREADY_PROCEEDING;


public class ChannelStatusAlreadyException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public ChannelStatusAlreadyException() {
        super(CHANNEL_STATUS_ALREADY_PROCEEDING.getMessage());
        this.exceptionCode = CHANNEL_STATUS_ALREADY_PROCEEDING;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
