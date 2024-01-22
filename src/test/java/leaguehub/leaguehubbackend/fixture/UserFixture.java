package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.domain.member.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.domain.email.entity.EmailAuth;
import leaguehub.leaguehubbackend.domain.member.entity.BaseRole;
import leaguehub.leaguehubbackend.domain.member.entity.LoginProvider;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserFixture {

    public static Member createMember() {
        Member member = Member.builder()
                .personalId("id").profileImageUrl("url")
                .nickname("id")
                .emailAuth(new EmailAuth("id@example.com", "authToken"))
                .loginProvider(LoginProvider.KAKAO).baseRole(BaseRole.USER)
                .build();
        member.verifyEmail();
        return member;
    }

    public static Member createGuestMember() {
        Member member = Member.builder()
                .personalId("Guest").profileImageUrl("urlGuest")
                .nickname("nickNameGuest")
                .emailAuth(new EmailAuth("idGuest@example.com", "authToken"))
                .loginProvider(LoginProvider.KAKAO).baseRole(BaseRole.GUEST)
                .build();

        return member;
    }

    public static Member createCustomeMember(String name){
        Member member = Member.builder()
                .personalId(name).profileImageUrl("url")
                .nickname(name)
                .loginProvider(LoginProvider.KAKAO).baseRole(BaseRole.USER)
                .build();

        return member;
    }



    public static LoginMemberResponse createLoginResponse() {
        LoginMemberResponse loginMemberResponse = LoginMemberResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        return loginMemberResponse;
    }

    public static void setUpAuth() {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username("id")
                .password("id")
                .roles("USER")
                .build();

        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null
                , authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    public static void setUpCustomAuth(String name) {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(name)
                .password("id")
                .roles("USER")
                .build();

        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null
                , authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void setUpCustomGuest(String name) {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(name)
                .password("id")
                .roles("GUEST")
                .build();

        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null
                , authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
