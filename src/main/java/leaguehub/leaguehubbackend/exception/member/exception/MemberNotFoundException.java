package leaguehub.leaguehubbackend.exception.member.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.global.exception.ResourceNotFoundException;

import static leaguehub.leaguehubbackend.exception.member.MemberExceptionCode.*;

public class MemberNotFoundException extends ResourceNotFoundException {

    private final ExceptionCode exceptionCode;

    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND);
        this.exceptionCode = MEMBER_NOT_FOUND;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
