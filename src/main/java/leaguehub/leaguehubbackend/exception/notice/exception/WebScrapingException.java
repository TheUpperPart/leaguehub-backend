package leaguehub.leaguehubbackend.exception.notice.exception;

import static leaguehub.leaguehubbackend.exception.notice.NoticeExceptionCode.WEB_SCRAPING_ERROR;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.exception.ResourceNotFoundException;

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