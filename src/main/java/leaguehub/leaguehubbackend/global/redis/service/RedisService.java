package leaguehub.leaguehubbackend.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void saveRefreshToken(String personalId, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(personalId, refreshToken, 7, TimeUnit.DAYS);
    }

    public String getRefreshToken(String personalId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(personalId);
    }

    public void deleteRefreshToken(String personalId) {
        redisTemplate.delete(personalId);
    }
}
