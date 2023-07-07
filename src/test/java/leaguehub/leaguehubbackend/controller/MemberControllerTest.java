package leaguehub.leaguehubbackend.controller;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("로그인한 상태에서 사용자 정보 요청시 사용자 정보 return")
    public void whenAuthenticated_thenReturnProfile() throws Exception {

        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn("12345");

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "");

        SecurityContextHolder.getContext().setAuthentication(auth);

        ProfileResponseDto mockResponse = ProfileResponseFixture.createProfileResponse();

        UserDetails userContext = SecurityUtils.getAuthenticatedUser();

        when(memberService.getMemberProfile(userContext.getUsername()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value("12345"))
                .andExpect(jsonPath("$.profileImageUrl").value("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1Y"))
                .andExpect(jsonPath("$.nickName").value("성우"));

    }
}
