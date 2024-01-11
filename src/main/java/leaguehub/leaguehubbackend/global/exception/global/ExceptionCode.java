package leaguehub.leaguehubbackend.global.exception.global;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {
    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
