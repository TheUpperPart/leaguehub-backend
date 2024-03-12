package leaguehub.leaguehubbackend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import leaguehub.leaguehubbackend.domain.member.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.fixture.UserFixture;
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
        Mockito.when(jwtService.createTokens(member.getPersonalId())).thenReturn(loginMemberResponse);
    }

    @Test
    @DisplayName("유효한 토큰을 받았을 때, 상태코드는 OK가 반환되어야 함")
    public void whenValidToken_thenReturnsOk() throws Exception {
        Mockito.when(jwtService.extractRefreshToken(Mockito.any())).thenReturn(Optional.of("validToken"));
        Mockito.when(jwtService.isTokenValid("validToken")).thenReturn(true);
        Mockito.when(jwtService.createTokens(member.getPersonalId())).thenReturn(loginMemberResponse);

        mockMvc.perform(post("/api/member/token")
                        .header("Authorization-refresh", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}