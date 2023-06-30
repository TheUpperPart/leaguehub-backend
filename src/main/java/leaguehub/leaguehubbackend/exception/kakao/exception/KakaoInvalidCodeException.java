package leaguehub.leaguehubbackend.exception.kakao.exception;

import leaguehub.leaguehubbackend.exception.global.ExceptionCode;
import leaguehub.leaguehubbackend.exception.kakao.KakaoExceptionCode;

import static leaguehub.leaguehubbackend.exception.kakao.KakaoExceptionCode.INVALID_KAKAO_CODE;

public class KakaoInvalidCodeException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public KakaoInvalidCodeException() {
        super(INVALID_KAKAO_CODE.getMessage());
        this.exceptionCode = KakaoExceptionCode.INVALID_KAKAO_CODE;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
