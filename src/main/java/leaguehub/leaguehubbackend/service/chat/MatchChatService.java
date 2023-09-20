package leaguehub.leaguehubbackend.service.chat;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.dto.chat.MatchMessage;
import leaguehub.leaguehubbackend.exception.chat.exception.MatchChatMessageConversionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchChatService {

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;
    private static final String REDIS_KEY_FORMAT = "channelId:%d:matchId:%d:messages";
    private static final String PUBLISH_KEY_FORMAT = "matchId:%d:messages";

    public void processMessage(MatchMessage message) {

        Long matchId = message.getMatchId();
        Long channelId = message.getChannelId();

        message.setTimestamp(LocalDateTime.now());

        String messageJson = convertMessageToJson(message);
        String redisKey = String.format(REDIS_KEY_FORMAT, channelId, matchId);
        String publishKey = String.format(PUBLISH_KEY_FORMAT, matchId);

        saveMessageToRedis(redisKey, messageJson);
        publishMessage(publishKey, messageJson);
    }

    private String convertMessageToJson(MatchMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("message로 변경 실패: {}", e.getMessage());
            throw new MatchChatMessageConversionException();
        }
    }

    private void saveMessageToRedis(String key, String messageJson) {
        stringRedisTemplate.opsForList().leftPush(key, messageJson);
    }

    private void publishMessage(String key, String messageJson) {
        stringRedisTemplate.convertAndSend(key, messageJson);
    }
}
