package leaguehub.leaguehubbackend.domain.match.service.chat;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.match.dto.MatchMessage;
import leaguehub.leaguehubbackend.domain.match.entity.MessageType;
import leaguehub.leaguehubbackend.domain.match.exception.chat.exception.MatchChatMessageConversionException;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchChatService {

    private static final String REDIS_KEY_FORMAT = "channelLink:%s:matchId:%d:messages";
    private static final String PUBLISH_KEY_FORMAT = "matchId:%d:messages";
    private static final String DELETE_CHANNEL_CHAT_FORMAT = "channelLink:%s:matchId:*:messages";

    private final StringRedisTemplate stringRedisTemplate;

    private final JwtService jwtService;

    private final MemberRepository memberRepository;

    private final ObjectMapper objectMapper;

    public void processMessage(MatchMessage message) {
        Long matchId = message.getMatchId();
        String channelLink = message.getChannelLink();

        message.setTimestamp(LocalDateTime.now());

        setAdminNameIfAdmin(message);

        String messageJson = convertMessageToJson(message);
        String redisKey = String.format(REDIS_KEY_FORMAT, channelLink, matchId);
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

    private void setAdminNameIfAdmin(MatchMessage message) {
        MessageType messageType = message.getType();
        if (messageType == MessageType.ADMIN) {
            String personalId = String.valueOf(jwtService.extractPersonalId(message.getAccessToken()));
            Optional<Member> memberOpt = memberRepository.findMemberByPersonalId(personalId);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                message.setAdminName(member.getNickname() + "(관리자)");
            } else {
                message.setAdminName("알 수 없는 관리자");
            }
        }
        message.setAccessToken(null);
    }

    public List<MatchMessage> findMatchChatHistory(String channelLink, Long matchId) {

        String targetMatch = String.format(REDIS_KEY_FORMAT, channelLink, matchId);

        List<String> messageList = stringRedisTemplate.opsForList().range(targetMatch, 0, -1);

        return messageList.stream()
                .map(this::convertJsonToMatchMessage)
                .collect(Collectors.toList());
    }

    private MatchMessage convertJsonToMatchMessage(String json) {
        try {
            return objectMapper.readValue(json, MatchMessage.class);
        } catch (JsonProcessingException e) {
            throw new MatchChatMessageConversionException();
        }
    }

    public void deleteChannelMatchChat(Channel channel) {
        String targetChannel = String.format(DELETE_CHANNEL_CHAT_FORMAT, channel.getChannelLink());
        Set<String> keys = stringRedisTemplate.keys(targetChannel);

        if (keys != null) {
            for (String key : keys) {
                stringRedisTemplate.delete(key);
            }
        }
    }

    public void processAdminAlert(String channelLink, Long matchId) {
        MatchMessage message = MatchMessage.builder()
                .channelLink(channelLink)
                .content("관리자 호출")
                .matchId(matchId)
                .participantId(-1L)
                .timestamp(LocalDateTime.now())
                .type(MessageType.ALERT)
                .build();

        processMessage(message);

    }
}
