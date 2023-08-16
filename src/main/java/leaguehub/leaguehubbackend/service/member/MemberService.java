package leaguehub.leaguehubbackend.service.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.dto.member.MypageResponseDto;
import leaguehub.leaguehubbackend.dto.member.NicknameRequestDto;
import leaguehub.leaguehubbackend.dto.member.ProfileDto;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantNotFoundException;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final ParticipantRepository participantRepository;

    private final JwtService jwtService;

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

        Member member = findCurrentMember();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            member.updateRefreshToken(null);
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
                .orElseGet(() -> saveMember(kakaoUserDto).orElseThrow(GlobalServerErrorException::new));
        return createLoginResponse(member);
    }

    public LoginMemberResponse createLoginResponse(Member member) {
        LoginMemberResponse loginMemberResponse = jwtService.createTokens(String.valueOf(member.getPersonalId()));
        loginMemberResponse.setVerifiedUser(member.getBaseRole() != BaseRole.GUEST);
        return loginMemberResponse;
    }


}

