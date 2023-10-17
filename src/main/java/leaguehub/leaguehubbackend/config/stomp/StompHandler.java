package leaguehub.leaguehubbackend.config.stomp;

import leaguehub.leaguehubbackend.exception.auth.exception.AuthExpiredTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthTokenNotFoundException;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtService jwtService;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            Optional<String> accessToken = jwtService.extractAccessToken(accessor);

            if (accessToken.isEmpty()) {
                log.info("Stomp : 토큰이 헤더에 없음");
                throw new AuthTokenNotFoundException();
            }
            if (jwtService.isTokenExpired(accessToken.get())) {
                log.info("Stomp : 토큰 기간 만료됨");
                throw new AuthExpiredTokenException();
            }
            if (!jwtService.isTokenValid(accessToken.get())) {
                log.info("Stomp : 유효하지 않은 토큰: " + accessToken.get());
                throw new AuthInvalidTokenException();
            }
        }
        return message;
    }
}