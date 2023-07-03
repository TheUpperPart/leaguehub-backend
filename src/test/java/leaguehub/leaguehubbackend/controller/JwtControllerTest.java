package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
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
    public void whenValidToken_thenReturnsOk() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.of("validToken"));
        Mockito.when(jwtService.isTokenValid("validToken")).thenReturn(true);
        Mockito.when(memberService.findMemberByRefreshToken("validToken")).thenReturn(Optional.of(member));
        Mockito.when(jwtService.createTokens(member.getPersonalId())).thenReturn(loginMemberResponse);
        mockMvc.perform(get("/api/reissue/token")
                        .header("Authorization-refresh", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void whenTokenInvalid_thenReturnsUnauthorized() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.of("invalidToken"));
        Mockito.when(jwtService.isTokenValid("invalidToken")).thenReturn(false);
        mockMvc.perform(get("/api/reissue/token")
                        .header("Authorization-refresh", "Bearer invalidToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value("AT-C-001"));
    }

    @Test
    public void whenMemberNotFound_thenReturnsNotFound() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.of("validToken"));
        Mockito.when(jwtService.isTokenValid("validToken")).thenReturn(true);
        Mockito.when(memberService.findMemberByRefreshToken("validToken")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/reissue/token")
                        .header("Authorization-refresh", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenNoToken_thenReturnsBadRequest() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/reissue/token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("AT-C-004"));
    }

}