package leaguehub.leaguehubbackend.service.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_ACCESS_TOKEN_TIME}")
    private Long accessTokenExpirationPeriod;

    @Value("${JWT_REFRESH_TOKEN_TIME}")
    private Long refreshTokenExpirationPeriod;

    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;


    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(String personalId) {
        Date now = new Date();
        return JWT.create()
                .withSubject("AccessToken")
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim("personalId", personalId)
                .sign(Algorithm.HMAC512(secretKey));
    }
    /**
     * Refresh 토큰 생성 메소드
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject("RefreshToken")
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * 헤더에서 RefreshToken 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization-refresh"))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * 헤더에서 AccessToken 추출
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }
    /**
     * AccessToken에서 PersonalId 추출
     */
    public Optional<String> extractPersonalId(String accessToken) {
        try {
            Long nickname = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim("personalId")
                    .asLong();
            return Optional.ofNullable(nickname != null ? String.valueOf(nickname) : null);
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }
    /**
     * AccessToken과 RefreshToken 생성
     */
    public LoginMemberResponse createTokens(String personalId) {
        String accessToken = createAccessToken(personalId);
        String refreshToken = createRefreshToken();
        updateRefreshToken(personalId, refreshToken);
        LoginMemberResponse tokenDto = LoginMemberResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return tokenDto;
    }
    /**
     * 매번 검증을 위한 매서드
     */
    public boolean isTokenValid(String token) {
        try {
            log.info("토큰 유효함");
            // 토큰 유효성 검증
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(String personalId, String refreshToken) {
        memberRepository.findMemberByPersonalId(personalId)
                .ifPresentOrElse(
                        member -> {
                            member.updateRefreshToken(refreshToken);
                            memberRepository.save(member);
                        },
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }

}
