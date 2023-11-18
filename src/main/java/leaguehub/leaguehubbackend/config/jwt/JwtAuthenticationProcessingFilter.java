package leaguehub.leaguehubbackend.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthExpiredTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthMemberNotFoundException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthTokenNotFoundException;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final MemberRepository memberRepository;
    private static final List<String> NO_CHECK_URLS = Arrays.asList(
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/api/member/oauth/kakao",
            "/api/member/token",
            "/api/member/oauth/**",
            "/verifiedPage.html",
            "/invalidPage.html",
            "/ws/**",
            "/app/**",
            "/api/notice/tft"
    );
    private static final List<String> NO_AUTH_URLS = Arrays.asList(
            "/api/match/*/player/info"
    );

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        // 체크 할필요 없는 url들을 다음 필터로 이동
        if (NO_CHECK_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (NO_AUTH_URLS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            try {
                checkAccessTokenAndAuthentication(request, response, filterChain);
            } catch (AuthTokenNotFoundException e) {
                saveAnonymousUserAuthentication();
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                request.setAttribute("exception", e.getMessage());
                throw e;
            }
        } else {
            try {
                checkAccessTokenAndAuthentication(request, response, filterChain);
            } catch (AuthInvalidTokenException | AuthExpiredTokenException | AuthMemberNotFoundException e) {
                request.setAttribute("exception", e.getMessage());
                throw e;
            }
        }
    }

    public void saveAnonymousUserAuthentication() {
        UserDetails anonymousUser = org.springframework.security.core.userdetails.User.builder()
                .username("anonymous")
                .password("anonymous")
                .roles("ANONYMOUS")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                anonymousUser, null, authoritiesMapper.mapAuthorities(anonymousUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {

        Optional<String> optionalToken = jwtService.extractAccessToken(request);

        if (optionalToken.isEmpty()) {
            log.info("AccessToken이 없음");
            throw new AuthTokenNotFoundException();
        }

        String accessToken = optionalToken.get();

        if (jwtService.isTokenExpired(accessToken)) {
            log.info("토큰 기간 만료됨");
            throw new AuthExpiredTokenException();
        }

        if (!jwtService.isTokenValid(accessToken)) {
            log.info("유효하지 않은 토큰: " + accessToken);
            throw new AuthInvalidTokenException();
        }

        Optional<String> optionalPersonalId = jwtService.extractPersonalId(accessToken);

        if (optionalPersonalId.isEmpty()) {
            log.info("해당 토큰에 personalId가 없음: " + accessToken);
            throw new AuthInvalidTokenException();
        }

        String personalId = optionalPersonalId.get();

        memberRepository.findMemberByPersonalId(personalId)
                .ifPresentOrElse(this::saveAuthentication, () -> {
                    log.info("해당 personalId를 가진 member없음: " + personalId);
                    throw new AuthMemberNotFoundException();
                });

        filterChain.doFilter(request, response);

    }

    public void saveAuthentication(Member member) {

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(member.getPersonalId())
                .password(member.getPersonalId())
                .roles(member.getBaseRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
