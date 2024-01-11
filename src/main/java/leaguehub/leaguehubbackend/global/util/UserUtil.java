package leaguehub.leaguehubbackend.global.util;

import leaguehub.leaguehubbackend.domain.member.entity.BaseRole;
import leaguehub.leaguehubbackend.domain.member.entity.LoginProvider;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    public String getUserPersonalId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    public void addDefaultUsers() {
        Member member1 = Member.builder()
                .personalId("1234")
                .nickname("member1이메일인증안됨")
                .profileImageUrl("https://robohash.org/1234?set=set2&size=180x180")
                .baseRole(BaseRole.GUEST)
                .loginProvider(LoginProvider.KAKAO)
                .build();

        Member member2 = Member.builder()
                .personalId("4321")
                .nickname("member2이메일인증됨")
                .profileImageUrl("https://robohash.org/4321?set=set2&size=180x180")
                .baseRole(BaseRole.USER)
                .emailUserVerified(true)
                .loginProvider(LoginProvider.KAKAO)
                .build();

        Member manager = Member.builder()
                .personalId("1")
                .nickname("관리자")
                .profileImageUrl("https://robohash.org/1?set=set2&size=180x180")
                .baseRole(BaseRole.ADMIN)
                .loginProvider(LoginProvider.KAKAO)
                .build();

        List<Member> members = Arrays.asList(
                member1,
                member2,
                manager
        );

        memberRepository.saveAll(members);

        createTokens(members);
    }
    public void createTokens(List<Member> members) {
        for (Member member : members) {
            String accessToken = jwtService.createAccessToken(member.getPersonalId());
            System.out.println("--------------------------");
            System.out.printf("해당 멤버 : '%s':%n", member.getNickname());
            System.out.printf("Access Token : Bearer %s%n", accessToken);
            System.out.println("--------------------------");
            System.out.println();

        }
    }
}
