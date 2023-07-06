package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.kakao.KakaoTokenResponseDto;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.kakao.exception.KakaoInvalidCodeException;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import leaguehub.leaguehubbackend.service.kakao.KakaoService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
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
        HttpHeaders headers = new HttpHeaders();

        assertThrows(KakaoInvalidCodeException.class, () -> {
            kakaoController.handleKakaoLogin(headers);
        });

        headers.add("Kakao-Code", "");

        assertThrows(KakaoInvalidCodeException.class, () -> {
            kakaoController.handleKakaoLogin(headers);
        });
    }

    @Test
    @DisplayName("유효한 카카오 코드시 200 Ok 응답")
    public void whenValidCode_thenReturnOk() throws Exception {
        KakaoTokenResponseDto kakaoTokenResponseDto = new KakaoTokenResponseDto();
        kakaoTokenResponseDto.setAccessToken("validToken");
        KakaoUserDto kakaoUserDto = new KakaoUserDto();
        Member member = UserFixture.createMember();
        LoginMemberResponse loginMemberResponse = UserFixture.createLoginResponse();

        when(kakaoService.getKakaoToken(anyString())).thenReturn(kakaoTokenResponseDto);
        when(kakaoService.getKakaoUser(kakaoTokenResponseDto)).thenReturn(kakaoUserDto);
        when(memberService.findMemberByPersonalId(String.valueOf(kakaoUserDto.getId()))).thenReturn(Optional.empty());
        when(memberService.saveMember(kakaoUserDto)).thenReturn(Optional.of(member));
        when(jwtService.createTokens(String.valueOf(kakaoUserDto.getId()))).thenReturn(loginMemberResponse);

        mockMvc.perform(get("/api/app/login/kakao")
                        .header("Kakao-Code", "testCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}