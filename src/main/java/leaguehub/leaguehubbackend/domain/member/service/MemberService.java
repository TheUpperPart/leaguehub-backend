package leaguehub.leaguehubbackend.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.domain.member.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.domain.member.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.domain.member.dto.member.MypageResponseDto;
import leaguehub.leaguehubbackend.domain.member.dto.member.NicknameRequestDto;
import leaguehub.leaguehubbackend.domain.member.dto.member.ProfileDto;
import leaguehub.leaguehubbackend.domain.member.entity.BaseRole;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantNotFoundException;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.global.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.global.redis.service.RedisService;
import leaguehub.leaguehubbackend.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final ParticipantRepository participantRepository;

    private final JwtService jwtService;

    private final RedisService redisService;

    public Optional<Member> findMemberByPersonalId(String personalId) {
        return memberRepository.findMemberByPersonalId(personalId);
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

        Member member = findCurrentMember();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            redisService.deleteRefreshToken(member.getPersonalId());
            SecurityContextHolder.clearContext();
            memberRepository.save(member);
        }
    }

    public ProfileDto getProfile() {

        Member member = findCurrentMember();

        return ProfileDto.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .build();
    }

    public MypageResponseDto getMypageProfile() {

        Member member = findCurrentMember();

        return MypageResponseDto.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .email(getVerifiedEmail(member))
                .userEmailVerified(member.isEmailUserVerified())
                .build();
    }

    public Member findCurrentMember() {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        return validateMember(userDetails.getUsername());
    }

    @Transactional
    public ProfileDto changeMemberParticipantNickname(NicknameRequestDto nicknameRequestDto) {

        Member member = findCurrentMember();

        member.updateNickname(nicknameRequestDto.getNickName());

        List<Participant> participants = participantRepository.findAllByMemberId(member.getId());
        if (participants.isEmpty()) {
            throw new ParticipantNotFoundException();
        }

        participants.forEach(participant -> participant.updateNickname(member.getNickname()));

        return getProfile();
    }

    @Transactional
    public LoginMemberResponse findOrSaveMember(KakaoUserDto kakaoUserDto) {
        Member member = memberRepository.findMemberByPersonalId(String.valueOf(kakaoUserDto.getId()))
                .map(existingMember -> updateProfileUrl(existingMember, kakaoUserDto))
                .orElseGet(() -> saveMember(kakaoUserDto).orElseThrow(GlobalServerErrorException::new));
        return createLoginResponse(member);
    }

    private Member updateProfileUrl(Member member, KakaoUserDto kakaoUserDto) {
        member.updateProfileImageUrl(kakaoUserDto.getKakaoAccount().getProfile().getThumbnailImageUrl());
        return member;
    }

    public LoginMemberResponse createLoginResponse(Member member) {
        LoginMemberResponse loginMemberResponse = jwtService.createTokens(String.valueOf(member.getPersonalId()));

        jwtService.updateRefreshToken(member.getPersonalId(), loginMemberResponse.getRefreshToken());

        loginMemberResponse.setVerifiedUser(member.getBaseRole() != BaseRole.GUEST);
        return loginMemberResponse;
    }

    public Boolean checkIfMemberIsAnonymous() {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if ("ROLE_ANONYMOUS".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}

