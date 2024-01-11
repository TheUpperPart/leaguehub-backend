package leaguehub.leaguehubbackend.domain.email.repository;

import leaguehub.leaguehubbackend.domain.email.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findAuthByEmail(String email);

}
