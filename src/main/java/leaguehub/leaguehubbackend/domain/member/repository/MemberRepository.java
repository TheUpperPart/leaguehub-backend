package leaguehub.leaguehubbackend.domain.member.repository;

import leaguehub.leaguehubbackend.domain.email.entity.EmailAuth;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findMemberByPersonalId(String personalId);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByRefreshToken(String refreshToken);
    @Query("SELECT m FROM Member m JOIN m.emailAuth e WHERE e.email = :email")
    Optional<Member> findMemberByEmail(@Param("email") String email);
    @Query("SELECT m FROM Member m WHERE m.emailAuth = :emailAuth")
    Optional<Member> findByEmailAuth(@Param("emailAuth") EmailAuth emailAuth);

}