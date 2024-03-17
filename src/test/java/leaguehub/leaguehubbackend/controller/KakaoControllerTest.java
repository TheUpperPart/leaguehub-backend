package leaguehub.leaguehubbackend.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import leaguehub.leaguehubbackend.domain.member.controller.KakaoController;
import leaguehub.leaguehubbackend.domain.member.dto.kakao.KakaoTokenResponseDto;
import leaguehub.leaguehubbackend.domain.member.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.domain.member.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import leaguehub.leaguehubbackend.domain.member.service.KakaoService;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
public class KakaoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private KakaoController kakaoController;

    @MockBean
    private KakaoService kakaoService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("유효하지 않은 카카오 코드시 KakaoInvalidCodeException")
    public void whenKakaoCodeIsMissingOrEmpty_thenThrowKakaoInvalidCodeException() throws Exception {
        mockMvc.perform(post("/api/member/oauth/kakao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/member/oauth/kakao")
                        .header("Kakao-Code", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효한 카카오 코드시 200 Ok 응답")
    public void whenValidCode_thenReturnOk() throws Exception {
        KakaoTokenResponseDto kakaoTokenResponseDto = new KakaoTokenResponseDto();
        kakaoTokenResponseDto.setAccessToken("validToken");
        KakaoUserDto kakaoUserDto = new KakaoUserDto();
        LoginMemberResponse loginMemberResponse = UserFixture.createLoginResponse();

        when(kakaoService.getKakaoToken(anyString())).thenReturn(kakaoTokenResponseDto);
        when(kakaoService.getKakaoUser(kakaoTokenResponseDto)).thenReturn(kakaoUserDto);
        when(memberService.findOrSaveMember(kakaoUserDto)).thenReturn(loginMemberResponse);

        mockMvc.perform(post("/api/member/oauth/kakao")
                        .header("Kakao-Code", "validCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유효한 카카오 코드로 로그인 시 200 OK와 헤더 반환")
    void whenValidKakaoCode_thenReturns200AndHeaders() throws Exception {
        String kakaoCode = "validKakaoCode";
        KakaoTokenResponseDto kakaoTokenResponseDto = new KakaoTokenResponseDto();
        kakaoTokenResponseDto.setAccessToken("validAccessToken");
        kakaoTokenResponseDto.setRefreshToken("validRefreshToken");
        kakaoTokenResponseDto.setExpiresIn(3600);
        kakaoTokenResponseDto.setRefreshTokenExpiresIn(14 * 24 * 3600);

        KakaoUserDto kakaoUserDto = new KakaoUserDto();
        LoginMemberResponse loginMemberResponse = LoginMemberResponse.builder()
                .accessToken("validAccessToken")
                .refreshToken("validRefreshToken")
                .verifiedUser(true)
                .build();

        given(kakaoService.getKakaoToken(kakaoCode)).willReturn(kakaoTokenResponseDto);
        given(kakaoService.getKakaoUser(kakaoTokenResponseDto)).willReturn(kakaoUserDto);
        given(memberService.findOrSaveMember(kakaoUserDto)).willReturn(loginMemberResponse);

        mockMvc.perform(post("/api/member/oauth/kakao")
                        .header("Kakao-Code", kakaoCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer validAccessToken"));
    }


}