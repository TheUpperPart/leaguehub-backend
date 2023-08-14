package leaguehub.leaguehubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.dto.member.MypageResponseDto;
import leaguehub.leaguehubbackend.dto.member.NicknameRequestDto;
import leaguehub.leaguehubbackend.dto.member.ProfileDto;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantNotFoundException;
import leaguehub.leaguehubbackend.fixture.MypageResponseFixture;
import leaguehub.leaguehubbackend.fixture.ProfileFixture;
import leaguehub.leaguehubbackend.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() throws IOException {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username("12345")
                .password("12345")
                .roles("USER")
                .build();

        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null
                , authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Test
    @DisplayName("로그인한 상태에서 /profile 요청시 사용자 프로필 정보 반환")
    public void whenAuthenticated_thenReturnProfile() throws Exception {

        ProfileDto mockProfileResponse = ProfileFixture.createProfile();

        when(memberService.getProfile())
                .thenReturn(mockProfileResponse);

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileImageUrl").value("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1Y"))
                .andExpect(jsonPath("$.nickName").value("성우"));
    }

    @Test
    @DisplayName("로그인한 상태에서 /mypage 요청시 사용자 마이페이지 정보 반환")
    public void whenAuthenticated_thenReturnMypage() throws Exception {

        MypageResponseDto mockMypageResponse = MypageResponseFixture.createMypageResponse();

        when(memberService.getMypageProfile())
                .thenReturn(mockMypageResponse);

        mockMvc.perform(get("/api/mypage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileImageUrl").value(mockMypageResponse.getProfileImageUrl()))
                .andExpect(jsonPath("$.nickName").value(mockMypageResponse.getNickName()))
                .andExpect(jsonPath("$.email").value(mockMypageResponse.getEmail()))
                .andExpect(jsonPath("$.userEmailVerified").value(mockMypageResponse.isUserEmailVerified()));
    }

    @Test
    @DisplayName("로그아웃 요청시 성공 메시지 return")
    void whenLogout_thenReturnSuccessMessage() throws Exception {

        doNothing().when(memberService).logoutMember(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class)
        );

        mockMvc.perform(post("/api/app/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout Success!"));

        verify(memberService).logoutMember(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class)
        );
    }
    @Test
    @DisplayName("닉네임 변경 요청시 프로필 정보 반환")
    public void whenChangeNickname_thenReturnProfile() throws Exception {

        NicknameRequestDto nicknameRequest = new NicknameRequestDto();
        nicknameRequest.setNickName("NewNickname");

        ProfileDto mockProfileResponse = ProfileFixture.createProfile();
        mockProfileResponse.setNickName("NewNickname");

        when(memberService.changeMemberParticipantNickname(nicknameRequest))
                .thenReturn(mockProfileResponse);

        mockMvc.perform(post("/api/change/nickname")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(nicknameRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickName").value("NewNickname"));
    }

}
