package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.email.EmailAuth;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.LoginProvider;
import leaguehub.leaguehubbackend.entity.member.Member;
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
                .nickname("id").refreshToken("refreshToken")
                .emailAuth(new EmailAuth("id@example.com", "authToken"))
                .loginProvider(LoginProvider.KAKAO).baseRole(BaseRole.USER)
                .build();

        return member;
    }

    public static Member createGuestMember() {
        Member member = Member.builder()
                .personalId("idGuest").profileImageUrl("urlGuest")
                .nickname("nickNameGuest").refreshToken("refreshTokenGuest")
                .emailAuth(new EmailAuth("idGuest@example.com", "authToken"))
                .loginProvider(LoginProvider.KAKAO).baseRole(BaseRole.GUEST)
                .build();

        return member;
    }

    public static Member createCustomeMember(String name){
        Member member = Member.builder()
                .personalId(name).profileImageUrl("url")
                .nickname(name).refreshToken("refreshToken")
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
}
