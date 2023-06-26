package leaguehub.leaguehubbackend.exception.channel.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.exception.ResourceNotFoundException;

import static leaguehub.leaguehubbackend.exception.channel.ChannelExceptionCode.CHANNEL_NOT_FOUND;


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
