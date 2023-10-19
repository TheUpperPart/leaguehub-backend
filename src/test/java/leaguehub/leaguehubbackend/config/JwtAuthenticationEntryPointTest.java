package leaguehub.leaguehubbackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.config.jwt.JwtAuthenticationEntryPoint;
import leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static leaguehub.leaguehubbackend.exception.auth.AuthExceptionCode.REQUEST_TOKEN_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtAuthenticationEntryPointTest {
    @InjectMocks
    private JwtAuthenticationEntryPoint entryPoint;
    @Mock
    private HttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    public void setUp() {
        response = new MockHttpServletResponse();
        authException = new AuthenticationException("Test Exception") {};
    }

    @Test
    @DisplayName("잘못된 요청인 경우 BAD_REQUEST_EXCEPTION")
    public void testCommence_badRequest() throws IOException {
        when(request.getAttribute("exception")).thenReturn(null);
        entryPoint.commence(request, response, authException);

        assertTrue(response.getContentAsString().contains(AuthExceptionCode.BAD_REQUEST_EXCEPTION.getMessage()));
    }

    @Test
    @DisplayName("토큰이 없는경우 REQUEST_TOKEN_NOT_FOUND")
    public void testCommence_noToken() throws IOException {
        when(request.getAttribute("exception")).thenReturn(REQUEST_TOKEN_NOT_FOUND.getCode());
        entryPoint.commence(request, response, authException);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertTrue(response.getContentAsString().contains(AuthExceptionCode.REQUEST_TOKEN_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("토큰이 만료된 경우 EXPIRED_TOKEN")
    public void testCommence_expiredToken() throws IOException {
        when(request.getAttribute("exception")).thenReturn(AuthExceptionCode.EXPIRED_TOKEN.getCode());
        entryPoint.commence(request, response, authException);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains(AuthExceptionCode.EXPIRED_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("토큰이 유효하지 않을 경우 INVALID_TOKEN")
    public void testCommence_invalidToken() throws IOException {
        when(request.getAttribute("exception")).thenReturn(AuthExceptionCode.INVALID_TOKEN.getCode());
        entryPoint.commence(request, response, authException);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains(AuthExceptionCode.INVALID_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("멤버가 없을 경우 AUTH_MEMBER_NOT_FOUND")
    public void testCommence_memberNotFound() throws IOException {
        when(request.getAttribute("exception")).thenReturn(AuthExceptionCode.AUTH_MEMBER_NOT_FOUND.getCode());
        entryPoint.commence(request, response, authException);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertTrue(response.getContentAsString().contains(AuthExceptionCode.AUTH_MEMBER_NOT_FOUND.getMessage()));
    }



}