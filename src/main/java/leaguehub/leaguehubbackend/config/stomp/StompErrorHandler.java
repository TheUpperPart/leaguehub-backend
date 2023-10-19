package leaguehub.leaguehubbackend.config.stomp;

import leaguehub.leaguehubbackend.exception.auth.exception.AuthExpiredTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthTokenNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import static leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode.*;
import static leaguehub.leaguehubbackend.exception.global.GlobalErrorCode.SERVER_ERROR;

@Component
@AllArgsConstructor
public class StompErrorHandler extends StompSubProtocolErrorHandler {
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(clientMessage);

        String errorMessage;

        if (ex.getCause() instanceof AuthExpiredTokenException) {
            errorMessage = EXPIRED_TOKEN.getMessage();
        } else if (ex.getCause() instanceof AuthInvalidTokenException) {
            errorMessage = INVALID_TOKEN.getMessage();
        } else if (ex.getCause() instanceof AuthTokenNotFoundException) {
            errorMessage = REQUEST_TOKEN_NOT_FOUND.getMessage();
        } else {
            errorMessage = SERVER_ERROR.getMessage();
        }

        byte[] errorPayload = errorMessage.getBytes();

        headerAccessor.setLeaveMutable(true);
        headerAccessor.setMessage(errorMessage);

        return MessageBuilder.createMessage(errorPayload, headerAccessor.getMessageHeaders());
    }
}
