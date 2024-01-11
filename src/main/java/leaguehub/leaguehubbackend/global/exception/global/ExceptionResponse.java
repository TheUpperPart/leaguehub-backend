package leaguehub.leaguehubbackend.global.exception.global;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionResponse {

    private Integer statusCode;
    private String code;
    private String message;
    private LocalDateTime timestamp;

    public ExceptionResponse(final ExceptionCode exceptionCode) {
        this.statusCode = exceptionCode.getHttpStatus().value();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }

}
