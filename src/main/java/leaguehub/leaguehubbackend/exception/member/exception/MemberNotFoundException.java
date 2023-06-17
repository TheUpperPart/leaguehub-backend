package leaguehub.leaguehubbackend.exception.member.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.member.MemberExceptionCode;

import static leaguehub.leaguehubbackend.exception.member.MemberExceptionCode.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND.getMessage());
        this.exceptionCode = MemberExceptionCode.MEMBER_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
