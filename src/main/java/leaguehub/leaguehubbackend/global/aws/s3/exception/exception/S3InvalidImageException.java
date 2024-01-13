package leaguehub.leaguehubbackend.global.aws.s3.exception.exception;

import leaguehub.leaguehubbackend.global.aws.s3.exception.S3ErrorCode;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

public class S3InvalidImageException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public S3InvalidImageException(){
        super(S3ErrorCode.INVALID_S3_IMAGE.getMessage());
        this.exceptionCode = S3ErrorCode.INVALID_S3_IMAGE;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
