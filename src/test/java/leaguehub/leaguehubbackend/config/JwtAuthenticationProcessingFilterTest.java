package leaguehub.leaguehubbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthExpiredTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthMemberNotFoundException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthTokenNotFoundException;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtAuthenticationProcessingFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.clearContext();
        jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
    }

    @Test
    @DisplayName("토큰이 유효하고 멤버가 존재할시 doFilterInternal은 정상작동을 한다")
    public void whenTokenIsValid_thenAuthenticationIsSet() throws ServletException, IOException {
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of("validToken"));
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractPersonalId(anyString())).thenReturn(Optional.of("personalId"));
        when(memberRepository.findMemberByPersonalId(anyString())).thenReturn(Optional.of(UserFixture.createMember()));

        jwtAuthenticationProcessingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 없을시 AuthTokenNotFoundException을 던진다")
    public void whenNoToken_thenThrowsAuthTokenNotFoundException() throws ServletException, IOException {
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.empty());

        assertThrows(AuthTokenNotFoundException.class, () -> {
            jwtAuthenticationProcessingFilter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    @DisplayName("토큰이 만료되었을시 AuthExpiredTokenException을 던진다")
    public void whenTokenIsExpired_thenThrowsAuthExpiredTokenException() throws ServletException, IOException {

        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of("expiredToken"));
        when(jwtService.isTokenExpired("expiredToken")).thenReturn(true);
        assertThrows(AuthExpiredTokenException.class, () -> {
            jwtAuthenticationProcessingFilter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    @DisplayName("토큰이 유효하지 않을시 AuthInvalidTokenException을 던진다")
    public void whenTokenIsInvalid_thenThrowsAuthInvalidTokenException() throws ServletException, IOException {
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of("invalidToken"));
        when(jwtService.isTokenExpired("invalidToken")).thenReturn(false);
        when(jwtService.isTokenValid("invalidToken")).thenReturn(false);

        assertThrows(AuthInvalidTokenException.class, () -> {
            jwtAuthenticationProcessingFilter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    @DisplayName("토큰에 있는 유저가 데이터베이스에 없을시 AuthMemberNotFoundException을 던진다")
    public void whenNoUser_thenThrowsAuthMemberNotFoundException() throws ServletException, IOException {
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of("validTokenNoUser"));
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractPersonalId(anyString())).thenReturn(Optional.of("noPersonalId"));
        when(memberRepository.findMemberByPersonalId(anyString())).thenReturn(Optional.empty());

        assertThrows(AuthMemberNotFoundException.class, () -> {
            jwtAuthenticationProcessingFilter.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    @DisplayName("filter에 등록된 url로 요청시 filter는 작동하지 않는다")
    public void whenUrlDoesNotNeedCheck_thenChainDoFilterCalled() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/app/login/kakao");

        jwtAuthenticationProcessingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰에 personalId가 없을시 AuthInvalidTokenException을 던진다")
    public void whenNoPersonalIdInToken_thenThrowsAuthInvalidTokenException() throws ServletException, IOException {
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of("validTokenNoPersonalId"));
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractPersonalId(anyString())).thenReturn(Optional.empty());

        assertThrows(AuthInvalidTokenException.class, () -> {
            jwtAuthenticationProcessingFilter.doFilterInternal(request, response, filterChain);
        });
    }

}