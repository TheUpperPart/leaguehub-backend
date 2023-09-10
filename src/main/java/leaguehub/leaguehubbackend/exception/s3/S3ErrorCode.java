package leaguehub.leaguehubbackend.exception.s3;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ExceptionCode {
    INVALID_S3_IMAGE(BAD_REQUEST, "S3-C-001", "유효하지 않은 이미지입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
