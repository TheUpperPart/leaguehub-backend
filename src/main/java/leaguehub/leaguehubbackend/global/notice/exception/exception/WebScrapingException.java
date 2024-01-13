package leaguehub.leaguehubbackend.global.notice.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.global.exception.global.exception.ResourceNotFoundException;

import static leaguehub.leaguehubbackend.global.notice.exception.NoticeExceptionCode.WEB_SCRAPING_ERROR;

public class WebScrapingException extends ResourceNotFoundException {

    private final ExceptionCode exceptionCode;

    public WebScrapingException() {
        super(WEB_SCRAPING_ERROR);
        this.exceptionCode = WEB_SCRAPING_ERROR;
    }

    @Override
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
