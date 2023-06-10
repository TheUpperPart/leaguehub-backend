package leaguehub.leaguehubbackend.exception.global.exception;

import jakarta.persistence.EntityNotFoundException;
import leaguehub.leaguehubbackend.exception.global.ExceptionCode;

public class ResourceNotFoundException extends EntityNotFoundException {

    private final ExceptionCode exceptionCode;

    public ResourceNotFoundException(
            ExceptionCode exceptionCode
    ) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
