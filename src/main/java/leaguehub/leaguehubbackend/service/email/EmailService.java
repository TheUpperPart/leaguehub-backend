package leaguehub.leaguehubbackend.service.email;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.entity.email.EmailAuth;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.email.exception.DuplicateEmailException;
import leaguehub.leaguehubbackend.exception.email.exception.InvalidEmailAddressException;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.repository.email.EmailAuthRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;

    private final EmailAuthRepository emailAuthRepository;

    private final MemberService memberService;

    private final ResourceLoader resourceLoader;

    @Value("${EMAIL_SECRET_KEY}")
    private String secretKey;

    @Value("${LEAGUE_HUB_ADDRESS}")
    private String leagueHubAddress;

    private final JavaMailSender mailSender;

    @Transactional
    public String sendEmailWithConfirmation(String email) {

        validateEmail(email);

        Member member = memberService.findCurrentMember();

        if (member.getEmailAuth() != null) {
            removeExistingEmailAuth(member);
        }

        String uniqueToken = generateUniqueTokenForUser(email);

        EmailAuth emailAuth = createAndSaveEmailAuth(email, member, uniqueToken);

        sendConfirmationEmail(emailAuth, uniqueToken);

        return email;
    }

    public void removeUnverifiedEmail(String email, Member member) {
        EmailAuth emailAuth = member.getEmailAuth();
        if (emailAuth != null) {
            emailAuthRepository.delete(emailAuth);
            member.assignEmailAuth(null);
            memberRepository.save(member);
        }
    }

    private void validateEmail(String email) {
        if (!isValidEmailFormat(email)) {
            throw new InvalidEmailAddressException();
        }

        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (!member.isEmailUserVerified()) {
                removeUnverifiedEmail(email, member);
            }
            if (member.isEmailUserVerified()) {
                throw new DuplicateEmailException();
            }
        }
    }

    public void sendConfirmationEmail(EmailAuth emailAuth, String uniqueToken) {
        try {
            String link = generateConfirmationLink(uniqueToken);
            String htmlTemplate = loadEmailTemplate("static/emailTemplate.html");
            String htmlContent = changeTemplate(htmlTemplate, link);
            sendEmail(emailAuth.getEmail(), "회원가입 이메일 인증", htmlContent);
        } catch (Exception e) {
            log.error("Error in sendConfirmationEmail", e);
            throw new GlobalServerErrorException();
        }
    }

    private String generateConfirmationLink(String uniqueToken) {
        return "http://" + leagueHubAddress + "/api/member/oauth/email?token=" + uniqueToken;
    }

    private String loadEmailTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        InputStream inputStream = resource.getInputStream();
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        return new String(bdata, StandardCharsets.UTF_8);
    }

    private String changeTemplate(String template, String link) {
        return template.replace("{{LINK}}", link);
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    private EmailAuth createAndSaveEmailAuth(String email, Member member, String uniqueToken) {
        EmailAuth emailAuth = new EmailAuth(email, uniqueToken);

        member.unverifyEmail();
        member.assignEmailAuth(emailAuth);

        emailAuthRepository.save(emailAuth);
        memberRepository.save(member);

        return emailAuth;
    }

    private void removeExistingEmailAuth(Member member) {
        emailAuthRepository.delete(member.getEmailAuth());
        member.assignEmailAuth(null);
    }

    public String generateUniqueTokenForUser(String email) {
        return JWT.create()
                .withSubject(email)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public String getEmailFromToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public boolean confirmUserEmail(String token) {
        try {

            String email = getEmailFromToken(token);

            if (email == null) {
                throw new RuntimeException("인증 토큰이 잘못되었습니다.");
            }

            EmailAuth emailAuth = emailAuthRepository.findAuthByEmail(email)
                    .orElseThrow(() -> new RuntimeException("인증 토큰이 잘못되었습니다."));

            if (emailAuth.getEmailExpireDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("인증 토큰이 만료되었습니다.");
            }

            Member member = memberRepository.findByEmailAuth(emailAuth)
                    .orElseThrow(() -> new RuntimeException("멤버 정보를 찾을 수 없습니다."));

            member.verifyEmail();

            if (member.getBaseRole() == BaseRole.GUEST) {
                member.updateRole(BaseRole.USER);
            }

            memberRepository.save(member);

            return true;
        } catch (Exception e) {
            log.error("이메일 링크 확인 중 에러 발생", e);
            return false;
        }
    }
}