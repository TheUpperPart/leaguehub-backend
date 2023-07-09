package leaguehub.leaguehubbackend.exception.participant.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import org.springframework.security.core.AuthenticationException;

import static leaguehub.leaguehubbackend.exception.participant.ParticipantExceptionCode.INVALID_PARTICIPANT_AUTH;

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
