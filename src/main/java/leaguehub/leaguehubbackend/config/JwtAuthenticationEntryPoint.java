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

import static leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode.AUTH_MEMBER_NOT_FOUND;


@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String exception = (String) request.getAttribute("exception");
        AuthExceptionCode errorCode;



        /**
         * 토큰 없는 경우
         */
        if (exception == null) {
            errorCode = AuthExceptionCode.REQUEST_TOKEN_NOT_FOUND;
            setResponse(response, errorCode);
            return;
        }

        /**
         *  해당 멤버가 데이터베이스에 없을 경우
         */
        if (exception.equals(AUTH_MEMBER_NOT_FOUND.getCode())) {
            errorCode = AUTH_MEMBER_NOT_FOUND;
            setResponse(response, errorCode);
            return;
        }

        /**
         * 토큰 만료된 경우
         */
        if (exception.equals(AuthExceptionCode.EXPIRED_TOKEN.getCode())) {
            errorCode = AuthExceptionCode.EXPIRED_TOKEN;
            setResponse(response, errorCode);
            return;
        }
        /**
         * 유효하지 않은 토큰일 경우
         */
        if (exception.equals(AuthExceptionCode.INVALID_TOKEN.getCode())) {
            errorCode = AuthExceptionCode.INVALID_TOKEN;
            setResponse(response, errorCode);
        }

    }

    private void setResponse(HttpServletResponse response, AuthExceptionCode errorCode) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(
                "{ " +
                        "\"statusCode\" : \"" + errorCode.getHttpStatus()
                        + "\", \"code\" : \"" + errorCode.getCode()
                        + "\", \"message\" : \"" + errorCode.getMessage()
                        + "\", \"timestamp\" : \"" + timestamp + "\""
                        + "}");
    }

}

