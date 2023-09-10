package leaguehub.leaguehubbackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode.*;


@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String exception = (String) request.getAttribute("exception");

        /**
         * 잘못된 요청
         */
        if (exception == null) {
            log.info("잘못된 요청");
            setResponse(response, BAD_REQUEST_EXCEPTION);
            return;
        }

        /**
         * 토큰 없는 경우
         */
        if (exception.equals(REQUEST_TOKEN_NOT_FOUND.getCode())) {
            log.info("AccessToken이 없음");
            setResponse(response, REQUEST_TOKEN_NOT_FOUND);
            return;
        }

        /**
         *  해당 멤버가 데이터베이스에 없을 경우
         */
        if (exception.equals(AUTH_MEMBER_NOT_FOUND.getCode())) {
            setResponse(response, AUTH_MEMBER_NOT_FOUND);
            return;
        }

        /**
         * 토큰 만료된 경우
         */
        if (exception.equals(EXPIRED_TOKEN.getCode())) {
            setResponse(response, EXPIRED_TOKEN);
            return;
        }

        /**
         * 유효하지 않은 토큰일 경우
         */
        if (exception.equals(INVALID_TOKEN.getCode())) {
            setResponse(response, INVALID_TOKEN);
        }

    }

    private void setResponse(HttpServletResponse response, AuthExceptionCode errorCode) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        response.getWriter().println(
                "{ " +
                        "\"statusCode\" : \"" + errorCode.getHttpStatus()
                        + "\", \"code\" : \"" + errorCode.getCode()
                        + "\", \"message\" : \"" + errorCode.getMessage()
                        + "\", \"timestamp\" : \"" + timestamp + "\""
                        + "}");
    }

}

