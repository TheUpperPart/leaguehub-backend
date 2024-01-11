package leaguehub.leaguehubbackend.exception.notice.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.exception.ResourceNotFoundException;

import static leaguehub.leaguehubbackend.exception.notice.NoticeExceptionCode.NOTICE_UNSUPPORTED;

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
