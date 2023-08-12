package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidRefreshToken;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.exception.auth.exception.AuthTokenNotFoundException;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
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
@Tag(name = "Jwt-Controller", description = "JWT 토큰 API")
public class JwtController {

    private final MemberService memberService;

    private final JwtService jwtService;

    @Operation(summary = "토큰 재발급", description = "refreshToken을 사용해서 accessToken 과 refreshToken 재발급")
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginMemberResponse.class))),
            @ApiResponse(responseCode = "400_1", description = "AT-C-004 요청에 토큰이 존재하지 않습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400_2", description = "AT-C-005 해당 리프레쉬 토큰을 가지는 멤버가 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "AT-C-001 유효하지 않은 토큰입니다.", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ExceptionResponse.class))),
    })
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
