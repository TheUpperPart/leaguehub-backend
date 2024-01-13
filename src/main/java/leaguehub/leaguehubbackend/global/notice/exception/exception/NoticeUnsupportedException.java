package leaguehub.leaguehubbackend.global.notice.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.global.exception.global.exception.ResourceNotFoundException;

import static leaguehub.leaguehubbackend.global.notice.exception.NoticeExceptionCode.NOTICE_UNSUPPORTED;

public class NoticeUnsupportedException extends ResourceNotFoundException {

    private final ExceptionCode exceptionCode;

    public NoticeUnsupportedException() {
        super(NOTICE_UNSUPPORTED);
        this.exceptionCode = NOTICE_UNSUPPORTED;
    }

    @Override
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
