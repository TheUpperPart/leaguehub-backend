package leaguehub.leaguehubbackend.service.email;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.entity.email.EmailAuth;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.email.exception.DuplicateEmailException;
import leaguehub.leaguehubbackend.exception.email.exception.InvalidEmailAddressException;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.email.EmailAuthRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private EmailAuthRepository emailAuthRepository;
    @Mock
    private JavaMailSender mailSender;
    private UserDetails userDetails;
    private Member member;

    @BeforeEach
    public void setUp() {
        userDetails = mock(UserDetails.class);
        member = UserFixture.createMember();
        ReflectionTestUtils.setField(emailService, "secretKey", "emailSecreetKey");
    }

    @Test
    @DisplayName("유효한 이메일형식 일때 이메일을 전송한다")
    void sendEmailWithConfirmation_whenValidEmailFormat() {

        String email = "id@example.com";

        String personalId = "id";

        when(userDetails.getUsername()).thenReturn(personalId);
        when(memberRepository.findMemberByPersonalId(personalId)).thenReturn(Optional.of(member));

        emailService.sendEmailWithConfirmation(email, userDetails);

        verify(emailAuthRepository, times(1)).save(any(EmailAuth.class));
    }

    @Test
    @DisplayName("유효하지 않은 이메일 형식 일때 InvalidEmailAddressException")
    void sendEmailWithConfirmation_whenNotValidEmailFormat() {

        when(userDetails.getUsername()).thenReturn("userName");

        String invalidEmail = "invalid-email-format";


        assertThrows(InvalidEmailAddressException.class, () -> emailService.sendEmailWithConfirmation(invalidEmail, userDetails));
    }

    @Test
    @DisplayName("이메일이 중복된 경우")
    void whenEmailAlreadyExists() {

        String email = "id@example.com";

        String personalId = "id";

        when(userDetails.getUsername()).thenReturn(personalId);
        when(memberRepository.findMemberByPersonalId(personalId)).thenReturn(Optional.of(member));
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(member));

        assertThrows(DuplicateEmailException.class, () -> emailService.sendEmailWithConfirmation(email, userDetails));
    }


    @Test
    @DisplayName("이메일 인증 토큰 생성")
    void generateUniqueTokenForUser() {

        String validToken = emailService.generateUniqueTokenForUser("test@example.com");

        assertNotNull(validToken);
    }

    @Test
    @DisplayName("이메일 인증이 이미 된 경우")
    void whenEmailAlreadyVerified() {

        String email = "id@example.com";

        member.verifyEmail();

        String personalId = "id";
        when(userDetails.getUsername()).thenReturn(personalId);
        when(memberRepository.findMemberByPersonalId(personalId)).thenReturn(Optional.of(member));

        emailService.sendEmailWithConfirmation(email, userDetails);
        verify(emailAuthRepository, times(1)).delete(any(EmailAuth.class));
    }

    @Test
    @DisplayName("토큰에서 이메일 추출")
    public void getEmailFromValidToken() {
        String expectedEmail = "test@example.com";
        String validToken = emailService.generateUniqueTokenForUser(expectedEmail);
        String actualEmail = emailService.getEmailFromToken(validToken);
        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 이메일 추출")
    public void getEmailFromInvalidToken() {
        String invalidToken = "invalidToken";
        String resultEmail = emailService.getEmailFromToken(invalidToken);
        assertNull(resultEmail);
    }

    @Test
    @DisplayName("토큰 유효할시 이메일 인증 후 BaseRole User인지 확인, true 반환")
    public void confirmUserEmailWithValidToken() {

        String validToken = emailService.generateUniqueTokenForUser(member.getEmailAuth().getEmail());

        EmailAuth emailAuth = EmailAuth.builder()
                .email(member.getEmailAuth().getEmail())
                .authToken(validToken)
                .build();

        when(emailAuthRepository.findAuthByEmail(member.getEmailAuth().getEmail())).thenReturn(Optional.of(emailAuth));
        when(memberRepository.findByEmailAuth(emailAuth)).thenReturn(Optional.of(member));

        boolean confirmationResult = emailService.confirmUserEmail(validToken);

        assertTrue(confirmationResult);
        assertEquals(BaseRole.USER, member.getBaseRole());
    }


    @Test
    @DisplayName("만료된 토큰일시 False 반환")
    public void confirmUserEmailWithExpiredToken() {

        String validToken = emailService.generateUniqueTokenForUser(member.getEmailAuth().getEmail());

        EmailAuth expiredEmailAuth = new EmailAuth("test@example.com", validToken);
        expiredEmailAuth.changeExpireDate(LocalDateTime.now().minusHours(1));

        when(emailAuthRepository.findAuthByEmail("test@example.com")).thenReturn(Optional.of(expiredEmailAuth));

        boolean confirmationResult = emailService.confirmUserEmail(validToken);

        assertFalse(confirmationResult);
    }

    @Test
    @DisplayName("emailAuth가 없을시 False 반환")
    public void confirmUserEmailWithNoEmailAuth() {

        Member member = UserFixture.createMember();
        String validToken = emailService.generateUniqueTokenForUser(member.getEmailAuth().getEmail());

        when(emailAuthRepository.findAuthByEmail(member.getEmailAuth().getEmail())).thenReturn(Optional.empty());

        boolean confirmationResult = emailService.confirmUserEmail(validToken);

        assertFalse(confirmationResult);
    }

    @Test
    @DisplayName("Member 정보가 없는 요청시 False 반환")
    public void confirmUserEmailWithNoMemberInfo() {

        String validToken = "someValidToken";

        EmailAuth emailAuth = new EmailAuth("test@example.com", validToken);

        when(emailAuthRepository.findAuthByEmail(emailAuth.getEmail())).thenReturn(Optional.of(emailAuth));
        when(memberRepository.findByEmailAuth(emailAuth)).thenReturn(Optional.empty());

        boolean confirmationResult = emailService.confirmUserEmail(validToken);

        assertFalse(confirmationResult);
    }

    @Test
    @DisplayName("Guest인 Member가 인증 성공시 User 로 Role 변경")
    public void confirmUserEmailWithGuestRole() {

        String validToken = emailService.generateUniqueTokenForUser("test@example.com");

        EmailAuth emailAuth = EmailAuth.builder()
                .email("test@example.com")
                .authToken(validToken)
                .build();

        Member member = UserFixture.createGuestMember();

        when(emailAuthRepository.findAuthByEmail(emailAuth.getEmail())).thenReturn(Optional.of(emailAuth));
        when(memberRepository.findByEmailAuth(emailAuth)).thenReturn(Optional.of(member));

        boolean confirmationResult = emailService.confirmUserEmail(validToken);

        assertTrue(confirmationResult);
        assertEquals(BaseRole.USER, member.getBaseRole());
    }
}
