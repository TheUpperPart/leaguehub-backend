package leaguehub.leaguehubbackend.service.jwt;

import leaguehub.leaguehubbackend.domain.member.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    @Mock
    private MemberRepository memberRepository;


    @BeforeEach
    public void setup(){
        ReflectionTestUtils.setField(jwtService, "secretKey", "testSecretKey");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationPeriod", 60L * 60 * 1000);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpirationPeriod", 24L * 60 * 60 * 1000);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유효한 토큰일시 true 반환")
    public void whenTokenValid_thenReturnTrue() {
        String testToken = jwtService.createAccessToken("testId");
        assertTrue(jwtService.isTokenValid(testToken));
    }

    @Test
    @DisplayName("유효하지 않은 토큰일시 false 반환")
    public void whenTokenInvalid_thenReturnFalse() {
        String invalidToken = "invalidToken";
        assertFalse(jwtService.isTokenValid(invalidToken));
    }

    @Test
    @DisplayName("Access 토큰 생성시 주어진 personalId가 토큰에 있어야함")
    public void whenCreateAccessToken_givenPersonalId_thenTokenShouldContainPersonalId() {
        String personalId = "testPersonalId";
        String accessToken = jwtService.createAccessToken(personalId);

        Optional<String> extractedPersonalId = jwtService.extractPersonalId(accessToken);

        assertTrue(extractedPersonalId.isPresent());
        assertEquals(personalId, extractedPersonalId.get());
    }

    @Test
    @DisplayName("토큰들 생성시 personalId가 주어졌을 때 로그인 response는 토큰들을 가지고 있어야함")
    public void whenCreateTokens_givenPersonalId_thenLoginMemberResponseShouldContainTokens() {
        String personalId = "testPersonalId";
        LoginMemberResponse loginMemberResponse = jwtService.createTokens(personalId);

        assertNotNull(loginMemberResponse.getAccessToken());
        assertNotNull(loginMemberResponse.getRefreshToken());
    }

    @Test
    @DisplayName("헤더에서 Access 토큰 추출시 유효하다면 true 반환")
    public void whenExtractAccessToken_givenValidTokenInHeader_thenShouldReturnToken() {
        String testToken = "testToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + testToken);

        Optional<String> extractedToken = jwtService.extractAccessToken(request);

        assertTrue(extractedToken.isPresent());
        assertEquals(testToken, extractedToken.get());
    }

    @Test
    @DisplayName("헤더에서 Refresh 토큰 추출시 유효하다면 true 반환")
    public void whenExtractRefreshToken_givenValidTokenInHeader_thenShouldReturnToken() {
        String testToken = "testToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization-refresh", "Bearer " + testToken);

        Optional<String> extractedToken = jwtService.extractRefreshToken(request);

        assertTrue(extractedToken.isPresent());
        assertEquals(testToken, extractedToken.get());
    }

    @Test
    public void whenIsTokenExpired_givenNonExpiredToken_thenShouldReturnFalse() {
        String personalId = "testPersonalId";
        String accessToken = jwtService.createAccessToken(personalId);

        assertFalse(jwtService.isTokenExpired(accessToken));
    }

}