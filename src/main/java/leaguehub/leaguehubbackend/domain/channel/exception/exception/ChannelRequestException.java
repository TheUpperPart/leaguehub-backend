package leaguehub.leaguehubbackend.domain.channel.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.channel.exception.ChannelExceptionCode.INVALID_REQUEST_CHANNEL;

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
