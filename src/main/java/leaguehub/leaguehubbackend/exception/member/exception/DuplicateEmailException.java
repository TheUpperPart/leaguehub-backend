package leaguehub.leaguehubbackend.exception.member.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.member.MemberExceptionCode.DUPLICATE_EMAIL_EXCEPTION;

public class DuplicateEmailException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public DuplicateEmailException() {
        super(DUPLICATE_EMAIL_EXCEPTION.getMessage());
        this.exceptionCode = DUPLICATE_EMAIL_EXCEPTION;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
