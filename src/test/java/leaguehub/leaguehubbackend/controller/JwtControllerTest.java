package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.domain.member.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtService jwtService;
    private Member member;
    private LoginMemberResponse loginMemberResponse;

    @BeforeEach
    public void setUp() {
        member = UserFixture.createMember();
        loginMemberResponse = UserFixture.createLoginResponse();
        Mockito.when(memberService.findMemberByRefreshToken(Mockito.any())).thenReturn(Optional.of(member));
        Mockito.when(jwtService.createTokens(member.getPersonalId())).thenReturn(loginMemberResponse);
    }

    @Test
    @DisplayName("유효한 토큰을 받았을 때, 상태코드는 OK가 반환되어야 함")
    public void whenValidToken_thenReturnsOk() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.of("validToken"));
        Mockito.when(jwtService.isTokenValid("validToken")).thenReturn(true);
        Mockito.when(memberService.findMemberByRefreshToken("validToken")).thenReturn(Optional.of(member));
        Mockito.when(jwtService.createTokens(member.getPersonalId())).thenReturn(loginMemberResponse);

        mockMvc.perform(post("/api/member/token")
                        .header("Authorization-refresh", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("토큰이 유효하지 않을 때, AT-C-001코드가 반환되어야 함")
    public void whenTokenInvalid_thenReturnsNotValid() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.of("invalidToken"));
        Mockito.when(jwtService.isTokenValid("invalidToken")).thenReturn(false);

        mockMvc.perform(post("/api/member/token")
                        .header("Authorization-refresh", "Bearer invalidToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AT-C-001"));
    }

    @Test
    @DisplayName("멤버가 존재하지 않을 때, 상태코드는 Not Found가 반환되어야 함")
    public void whenMemberNotFound_thenReturnsNotFound() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.of("validToken"));
        Mockito.when(jwtService.isTokenValid("validToken")).thenReturn(true);
        Mockito.when(memberService.findMemberByRefreshToken("validToken")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/member/token")
                        .header("Authorization-refresh", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("AT-C-005"));
    }

    @Test
    @DisplayName("토큰이 존재하지 않을 때, AT-C-004코드가 반환되어야 함")
    public void whenNoToken_thenReturnsBadRequest() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/member/token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("AT-C-004"));
    }

}