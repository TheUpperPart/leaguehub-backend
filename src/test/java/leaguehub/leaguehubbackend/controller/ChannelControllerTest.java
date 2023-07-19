package leaguehub.leaguehubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.controller.channel.ChannelController;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ChannelController channelController;

    @Autowired
    ChannelService channelService;

    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @MockBean
    private JwtService jwtService;
    private Member member;
    private LoginMemberResponse loginMemberResponse;

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
    public void testCreateChannel() throws Exception {

        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        String json = objectMapper.writeValueAsString(createChannelDto);
        System.out.println(json);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @DisplayName("채널 바인딩 에러 테스트 - 컨트롤러")
    @WithMockUser
    void createChannelControllerError() {

    }


}