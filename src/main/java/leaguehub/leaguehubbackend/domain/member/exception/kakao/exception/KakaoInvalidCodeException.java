package leaguehub.leaguehubbackend.domain.member.exception.kakao.exception;

import leaguehub.leaguehubbackend.domain.member.exception.kakao.KakaoExceptionCode;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionCode;

import static leaguehub.leaguehubbackend.domain.member.exception.kakao.KakaoExceptionCode.INVALID_KAKAO_CODE;

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
