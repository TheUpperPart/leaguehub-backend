package leaguehub.leaguehubbackend.exception.member;

import jakarta.persistence.EntityNotFoundException;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.RequiredArgsConstructor;

import static leaguehub.leaguehubbackend.exception.member.MemberExceptionCode.*;

public class MemberNotFoundException extends EntityNotFoundException {

    private final ExceptionCode exceptionCode;

    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND.getMessage());
        exceptionCode = MEMBER_NOT_FOUND;
    }


}
