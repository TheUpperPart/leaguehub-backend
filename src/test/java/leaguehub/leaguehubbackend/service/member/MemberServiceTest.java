package leaguehub.leaguehubbackend.service.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.dto.member.MypageResponseDto;
import leaguehub.leaguehubbackend.dto.member.ProfileDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.fixture.KakaoUserDtoFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;
    private Member member;
    private Member expectedMember;
    @BeforeEach
    public void setUp() {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username("id")
                .password("id")
                .roles("USER")
                .build();

        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null
                , authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        member = UserFixture.createMember();

        expectedMember = UserFixture.createMember();

    }
    @Test
    @DisplayName("개인 아이디로 멤버 찾기")
    void findMemberByPersonalId() {

        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(expectedMember));

        Optional<Member> actualMember = memberService.findMemberByPersonalId("id");

        assertTrue(actualMember.isPresent());
        assertEquals(expectedMember, actualMember.get());
    }

    @Test
    @DisplayName("리프레시 토큰으로 멤버 찾기")
    void findMemberByRefreshToken() {

        when(memberRepository.findByRefreshToken("refreshToken")).thenReturn(Optional.of(expectedMember));

        Optional<Member> actualMember = memberService.findMemberByRefreshToken("refreshToken");

        assertTrue(actualMember.isPresent());
        assertEquals(expectedMember, actualMember.get());
    }

    @Test
    @DisplayName("새로운 멤버 저장")
    void saveMember() {

        KakaoUserDto kakaoUserDto = KakaoUserDtoFixture.createKakaoUserDto();
        expectedMember = Member.kakaoUserToMember(KakaoUserDtoFixture.createKakaoUserDto());
        when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

        Optional<Member> actualMember = memberService.saveMember(kakaoUserDto);

        assertTrue(actualMember.isPresent());
        assertEquals(expectedMember.getId(), actualMember.get().getId());
        assertEquals(expectedMember.getPersonalId(), actualMember.get().getPersonalId());
        assertEquals(expectedMember.getRefreshToken(), actualMember.get().getRefreshToken());
        assertEquals(expectedMember.getLoginProvider(), actualMember.get().getLoginProvider());

    }

    @Test
    @DisplayName("개인 아이디로 멤버 유효성 검사")
    void validateMember() {

        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(expectedMember));

        Member actualMember = memberService.validateMember("id");
        assertEquals(expectedMember, actualMember);

        when(memberRepository.findMemberByPersonalId(eq("invalidId"))).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.validateMember("invalidId"));
    }

    @Test
    @DisplayName("개인 아이디로 멤버 마이페이지 정보 가져오기")
    void getProfileTest() {

        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(member));

        ProfileDto profile = memberService.getProfile();

        assertEquals(member.getNickname(), profile.getNickName());

    }

    @Test
    @DisplayName("유효한 멤버의 마이페이지 프로필 검색")
    void getMypageProfile_ValidMember() {

        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(member));

        MypageResponseDto mypageProfile = memberService.getMypageProfile();

        assertEquals(member.getProfileImageUrl(), mypageProfile.getProfileImageUrl());
        assertEquals(member.getNickname(), mypageProfile.getNickName());
        assertEquals(member.isEmailUserVerified(), mypageProfile.isUserEmailVerified());
        assertEquals(memberService.getVerifiedEmail(member), mypageProfile.getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 멤버의 마이페이지 프로필 검색")
    void getMypageProfile_InvalidMember() {
        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMypageProfile());
    }
    @Test
    @DisplayName("멤버 로그아웃")
    void logoutMemberTest() {

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(member));

        memberService.logoutMember(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(memberRepository).save(any(Member.class));
    }

}