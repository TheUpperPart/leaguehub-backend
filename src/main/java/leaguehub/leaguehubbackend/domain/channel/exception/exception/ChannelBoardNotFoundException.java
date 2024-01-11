package leaguehub.leaguehubbackend.domain.channel.exception.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.exception.ResourceNotFoundException;

import static leaguehub.leaguehubbackend.domain.channel.exception.ChannelExceptionCode.CHANNEL_BOARD_NOT_FOUND;

public class ChannelBoardNotFoundException extends ResourceNotFoundException {

    private final ExceptionCode exceptionCode;

    public ChannelBoardNotFoundException() {
        super(CHANNEL_BOARD_NOT_FOUND);
        this.exceptionCode = CHANNEL_BOARD_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
