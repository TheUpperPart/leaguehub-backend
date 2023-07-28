package leaguehub.leaguehubbackend.service.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.dto.member.ProfileResponseDto;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.Member;

import leaguehub.leaguehubbackend.exception.member.exception.DuplicateEmailException;
import leaguehub.leaguehubbackend.exception.member.exception.InvalidEmailAddressException;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;

import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.regex.Pattern;

import static leaguehub.leaguehubbackend.exception.member.MemberExceptionCode.INVALID_EMAIL_ADDRESS;

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

    public ProfileResponseDto getMemberProfile(String personalId) {
        Member member = memberRepository.findMemberByPersonalId(personalId)
                .orElseThrow(MemberNotFoundException::new);

        return ProfileResponseDto.builder()
                .profileId(member.getPersonalId())
                .profileImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .build();
    }

    public void logoutMember(String personalId, UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberRepository.findMemberByPersonalId(personalId)
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

    @Transactional
    public void updateEmail(String email, String personalId) {

        if (!isValidEmailFormat(email)) {
            throw new InvalidEmailAddressException();
        }

        if (memberRepository.findMemberByEmail(email).isPresent()) {
            throw new DuplicateEmailException();
        }

        Member member = memberRepository.findMemberByPersonalId(personalId)
                .orElseThrow(MemberNotFoundException::new);
        member.updateEmail(email);
        member.updateRole(BaseRole.USER);

        memberRepository.save(member);
    }

    public static boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
}

