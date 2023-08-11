package leaguehub.leaguehubbackend.service.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.dto.member.MypageResponseDto;
import leaguehub.leaguehubbackend.dto.member.ProfileDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Optional<Member> findMemberByPersonalId(String personalId) {
        return memberRepository.findMemberByPersonalId(personalId);
    }

    public Optional<Member> findMemberByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken);
    }

    @Transactional
    public Optional<Member> saveMember(KakaoUserDto kakaoUserDto) {
        Member newUser = Member.kakaoUserToMember(kakaoUserDto);
        memberRepository.save(newUser);
        return Optional.of(newUser);
    }

    public Member validateMember(String personalId) {
        Member member = memberRepository.findMemberByPersonalId(personalId)
                .orElseThrow(MemberNotFoundException::new);
        return member;
    }

    public String getVerifiedEmail(Member member) {
        if (member.getEmailAuth() != null && member.isEmailUserVerified()) {
            return member.getEmailAuth().getEmail();
        }
        return "N/A";
    }

    public void logoutMember(HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        Member member = memberRepository.findMemberByPersonalId(userDetails.getUsername())
                .orElseThrow(MemberNotFoundException::new);

        if (member.getPersonalId().equals(userDetails.getUsername())) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null){
                new SecurityContextLogoutHandler().logout(request, response, auth);
                member.updateRefreshToken(null);
                SecurityContextHolder.clearContext();
                memberRepository.save(member);
            }
        } else {
            throw new MemberNotFoundException();
        }
    }

    public ProfileDto getProfile() {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        Member member = memberRepository.findMemberByPersonalId(userDetails.getUsername())
                .orElseThrow(MemberNotFoundException::new);

        return ProfileDto.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .build();
    }

    public MypageResponseDto getMypageProfile() {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        Member member = memberRepository.findMemberByPersonalId(userDetails.getUsername())
                .orElseThrow(MemberNotFoundException::new);

        return MypageResponseDto.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .email(getVerifiedEmail(member))
                .userEmailVerified(member.isEmailUserVerified())
                .build();
    }
}

