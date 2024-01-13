package leaguehub.leaguehubbackend.domain.participant.exception.exception;

import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.domain.participant.exception.ParticipantExceptionCode.INVALID_PARTICIPANT_AUTH;

public class InvalidParticipantAuthException extends AuthenticationException {

    private final ExceptionCode exceptionCode;

    public InvalidParticipantAuthException() {
        super(INVALID_PARTICIPANT_AUTH.getCode());
        this.exceptionCode = INVALID_PARTICIPANT_AUTH;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
