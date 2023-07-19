package leaguehub.leaguehubbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidRefreshToken;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthTokenNotFoundException;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class JwtController {

    private final MemberService memberService;

    private final JwtService jwtService;

    @GetMapping("/reissue/token")
    public ResponseEntity<LoginMemberResponse> refreshAccessToken(HttpServletRequest request) {

        Optional<String> optionalToken = jwtService.extractRefreshToken(request);

        String refreshToken = optionalToken.orElseThrow(() -> {
            log.info("요청헤더에 refreshToken 없음: ");
            return new AuthTokenNotFoundException();
        });

        Optional<Member> memberOpt;
        if (jwtService.isTokenValid(refreshToken)) {
            memberOpt = memberService.findMemberByRefreshToken(refreshToken);
        } else {
            log.info("유효하지 않은 토큰: " + refreshToken);
            throw new AuthInvalidTokenException();
        }

        Member member = memberOpt.orElseThrow(() -> {
            log.info("해당 refreshToken을 가지고 있는 멤버 없음: " + refreshToken);
            return new AuthInvalidRefreshToken();
        });

        LoginMemberResponse loginMemberResponse = jwtService.createTokens(member.getPersonalId());

        return ResponseEntity.ok(loginMemberResponse);
    }
}
