package leaguehub.leaguehubbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.dto.member.ProfileResponseDto;
import leaguehub.leaguehubbackend.fixture.ProfileResponseFixture;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import org.junit.jupiter.api.*;
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
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

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
    @DisplayName("로그인한 상태에서 사용자 정보 요청시 사용자 정보 return")
    public void whenAuthenticated_thenReturnProfile() throws Exception {

        ProfileResponseDto mockResponse = ProfileResponseFixture.createProfileResponse();

        UserDetails userDetail = SecurityUtils.getAuthenticatedUser();

        when(memberService.getMemberProfile(userDetail.getUsername()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value("12345"))
                .andExpect(jsonPath("$.profileImageUrl").value("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1Y"))
                .andExpect(jsonPath("$.nickName").value("성우"));

    }

    @Test
    @DisplayName("로그아웃 요청시 성공 메시지 return")
    public void whenLogout_thenReturnSuccessMessage() throws Exception {

        UserDetails userDetail = SecurityUtils.getAuthenticatedUser();

        doNothing().when(memberService).logoutMember(
                eq(userDetail.getUsername()),
                eq(userDetail),
                any(HttpServletRequest.class),
                any(HttpServletResponse.class)
        );

        mockMvc.perform(post("/api/app/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout Success!"));

        verify(memberService).logoutMember(
                eq(userDetail.getUsername()),
                eq(userDetail),
                any(HttpServletRequest.class),
                any(HttpServletResponse.class)
        );
    }
}
