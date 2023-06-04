package leaguehub.leaguehubbackend.repository.member;

import leaguehub.leaguehubbackend.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findMemberByPersonalId(String personalId);
}