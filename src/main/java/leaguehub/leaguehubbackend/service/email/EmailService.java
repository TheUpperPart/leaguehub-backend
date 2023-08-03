package leaguehub.leaguehubbackend.service.email;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.entity.email.EmailAuth;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.email.exception.DuplicateEmailException;
import leaguehub.leaguehubbackend.exception.email.exception.InvalidEmailAddressException;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.repository.email.EmailAuthRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final MemberRepository memberRepository;

    private final EmailAuthRepository emailAuthRepository;

    @Value("${EMAIL_SECRET_KEY}")
    private String secretKey;

    @Value("${LEAGUE_HUB_ADDRESS}")
    private String leagueHubAddress;

    private final JavaMailSender mailSender;

    @Transactional
    public void sendEmailWithConfirmation(String email, UserDetails userDetails) {

        validateEmail(email);

        Member member = memberRepository.findMemberByPersonalId(userDetails.getUsername())
                .orElseThrow(MemberNotFoundException::new);

        removeExistingEmailAuth(member);

        String uniqueToken = generateUniqueTokenForUser(email);

        EmailAuth emailAuth = createAndSaveEmailAuth(email, member, uniqueToken);

        sendConfirmationEmail(emailAuth, uniqueToken);

    }

    private void validateEmail(String email) {
        if (!isValidEmailFormat(email)) throw new InvalidEmailAddressException();
        if (memberRepository.findMemberByEmail(email).isPresent()) throw new DuplicateEmailException();
    }
    private void sendConfirmationEmail(EmailAuth emailAuth, String uniqueToken) {
        try {
            String link = "http://" + leagueHubAddress + "/confirm/Email?token=" + uniqueToken;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailAuth.getEmail());
            message.setSubject("회원가입 이메일 인증");
            message.setText(link);
            mailSender.send(message);
        } catch (Exception e) {
            throw new GlobalServerErrorException();
        }
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
        if (member.isEmailUserVerified() && member.getEmailAuth() != null) {
            emailAuthRepository.delete(member.getEmailAuth());
            member.assignEmailAuth(null);
        }
    }

    private String generateUniqueTokenForUser(String email) {
        return JWT.create()
                .withSubject(email)
                .sign(Algorithm.HMAC256(secretKey));
    }
    public boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    @Transactional
    public boolean confirmUserEmail(String token) {
        try {
            String email = JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token)
                    .getSubject();

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