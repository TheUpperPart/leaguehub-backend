package leaguehub.leaguehubbackend.exception.member.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.exception.member.MemberExceptionCode.INVALID_EMAIL_ADDRESS;

public class InvalidEmailAddressException extends IllegalArgumentException  {
    private final ExceptionCode exceptionCode;

    public InvalidEmailAddressException() {

        super(INVALID_EMAIL_ADDRESS.getMessage());
        this.exceptionCode = INVALID_EMAIL_ADDRESS;

    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
