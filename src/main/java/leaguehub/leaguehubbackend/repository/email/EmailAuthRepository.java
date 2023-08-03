package leaguehub.leaguehubbackend.repository.email;

import leaguehub.leaguehubbackend.entity.email.EmailAuth;
import leaguehub.leaguehubbackend.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findAuthByEmail(String email);

}
