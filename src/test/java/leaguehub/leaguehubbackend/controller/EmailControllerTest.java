package leaguehub.leaguehubbackend.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import leaguehub.leaguehubbackend.domain.email.dto.EmailDto;
import leaguehub.leaguehubbackend.domain.email.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

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
    public void testSendEmailWithConfirmation() throws Exception {
        String email = "test@email.com";
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(email);

        when(emailService.sendEmailWithConfirmation(emailDto.getEmail()))
                .thenReturn(email);

        mockMvc.perform(post("/api/member/auth/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(emailDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email Successfully Sent to " + email));

        verify(emailService, times(1)).sendEmailWithConfirmation(emailDto.getEmail());
    }

    @Test
    public void testConfirmUserEmail_validToken() throws Exception {
        String validToken = "validToken";
        when(emailService.confirmUserEmail(validToken)).thenReturn(true);

        mockMvc.perform(get("/api/member/oauth/email?token=" + validToken))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage"));
    }

    @Test
    public void testConfirmUserEmail_invalidToken() throws Exception {
        String invalidToken = "invalidToken";
        when(emailService.confirmUserEmail(invalidToken)).thenReturn(false);

        mockMvc.perform(get("/api/member/oauth/email?token=" + invalidToken))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}