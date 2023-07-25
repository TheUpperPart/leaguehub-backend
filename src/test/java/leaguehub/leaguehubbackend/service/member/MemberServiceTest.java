package leaguehub.leaguehubbackend.service.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.dto.member.ProfileResponseDto;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.member.exception.DuplicateEmailException;
import leaguehub.leaguehubbackend.exception.member.exception.InvalidEmailAddressException;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.fixture.KakaoUserDtoFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberService memberService;

    @Test
    void findMemberByPersonalId() {
        Member expectedMember = UserFixture.createMember();
        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(expectedMember));

        Optional<Member> actualMember = memberService.findMemberByPersonalId("id");

        assertTrue(actualMember.isPresent());
        assertEquals(expectedMember, actualMember.get());
    }

    @Test
    void findMemberByRefreshToken() {
        Member expectedMember = UserFixture.createMember();
        when(memberRepository.findByRefreshToken("refreshToken")).thenReturn(Optional.of(expectedMember));

        Optional<Member> actualMember = memberService.findMemberByRefreshToken("refreshToken");

        assertTrue(actualMember.isPresent());
        assertEquals(expectedMember, actualMember.get());
    }

    @Test
    void saveMember() {
        KakaoUserDto kakaoUserDto = KakaoUserDtoFixture.createKakaoUserDto();
        Member expectedMember = Member.kakaoUserToMember(KakaoUserDtoFixture.createKakaoUserDto()); 
        when(memberRepository.save(any(Member.class))).thenReturn(expectedMember);

        Optional<Member> actualMember = memberService.saveMember(kakaoUserDto);

        assertTrue(actualMember.isPresent());
        assertEquals(expectedMember.getId(), actualMember.get().getId());
        assertEquals(expectedMember.getPersonalId(), actualMember.get().getPersonalId());
        assertEquals(expectedMember.getRefreshToken(), actualMember.get().getRefreshToken());
        assertEquals(expectedMember.getLoginProvider(), actualMember.get().getLoginProvider());

    }

    @Test
    void validateMember() {
        Member expectedMember = UserFixture.createMember();
        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(expectedMember));

        Member actualMember = memberService.validateMember("id");
        assertEquals(expectedMember, actualMember);

        when(memberRepository.findMemberByPersonalId(eq("invalidId"))).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.validateMember("invalidId"));
    }

    @Test
    void getMemberProfile() {
        Member testMember = UserFixture.createMember();
        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(testMember));

        ProfileResponseDto profile = memberService.getMemberProfile("id");

        assertEquals(testMember.getPersonalId(), profile.getProfileId());
        assertEquals(testMember.getProfileImageUrl(), profile.getProfileImageUrl());
        assertEquals(testMember.getNickname(), profile.getNickName());

        when(memberRepository.findMemberByPersonalId("invalidId")).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberProfile("invalidId"));
    }

    @Test
    void logoutMember() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        UserDetails userDetails = mock(UserDetails.class);
        Member testMember = UserFixture.createMember();
        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(testMember));
        when(userDetails.getUsername()).thenReturn("id");

        Authentication auth = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        memberService.logoutMember("id", userDetails, request, response);

        verify(memberRepository).save(testMember);

    }

    @Test
    void logoutMember_WhenUsernamesDoNotMatch() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        UserDetails userDetails = mock(UserDetails.class);
        Member testMember = UserFixture.createMember();
        when(memberRepository.findMemberByPersonalId("id")).thenReturn(Optional.of(testMember));
        when(userDetails.getUsername()).thenReturn("otherId");

        Authentication auth = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(MemberNotFoundException.class, () -> memberService.logoutMember("id", userDetails, request, response));
    }

    @Test
    void updateEmailWithValidEmail() {
        String testEmail = "test@example.com";
        String testId = "12345678";

        Member testMember = UserFixture.createMember();
        when(memberRepository.findMemberByEmail(testEmail)).thenReturn(Optional.empty());
        when(memberRepository.findMemberByPersonalId(testId)).thenReturn(Optional.of(testMember));

        assertDoesNotThrow(() -> memberService.updateEmail(testEmail, testId));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateEmailWithInvalidEmail() {
        String testEmail = "invalidemail";
        String testId = "12345678";

        assertThrows(InvalidEmailAddressException.class, () -> memberService.updateEmail(testEmail, testId));
    }

    @Test
    void updateEmailWithDuplicateEmail() {
        String testEmail = "id@example.com";
        String testId = "12345678";

        Member duplicateMember = UserFixture.createMember();

        when(memberRepository.findMemberByEmail(testEmail)).thenReturn(Optional.of(duplicateMember));

        assertThrows(DuplicateEmailException.class, () -> memberService.updateEmail(testEmail, testId));
    }


}