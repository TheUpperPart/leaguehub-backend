package leaguehub.leaguehubbackend.domain.channel.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.global.exception.global.exception.ResourceNotFoundException;

import static leaguehub.leaguehubbackend.domain.channel.exception.ChannelExceptionCode.CHANNEL_NOT_FOUND;


public class ChannelNotFoundException extends ResourceNotFoundException {

    private final ExceptionCode exceptionCode;

    public ChannelNotFoundException() {
        super(CHANNEL_NOT_FOUND);
        this.exceptionCode = CHANNEL_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
