package leaguehub.leaguehubbackend.exception.s3.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.s3.S3ErrorCode;

import static leaguehub.leaguehubbackend.exception.s3.S3ErrorCode.INVALID_S3_IMAGE;

public class S3InvalidImageException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public S3InvalidImageException(){
        super(INVALID_S3_IMAGE.getMessage());
        this.exceptionCode = INVALID_S3_IMAGE;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
