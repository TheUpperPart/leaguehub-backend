package leaguehub.leaguehubbackend.domain.member.controller;

import static org.springframework.http.HttpStatus.OK;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.function.Predicate;
import leaguehub.leaguehubbackend.domain.member.dto.kakao.KakaoTokenResponseDto;
import leaguehub.leaguehubbackend.domain.member.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.domain.member.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.domain.member.exception.kakao.exception.KakaoInvalidCodeException;
import leaguehub.leaguehubbackend.domain.member.service.KakaoService;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Kakao-Controller", description = "카카오 API")
public class KakaoController {

    private final KakaoService kakaoService;

    private final MemberService memberService;
    @Operation(summary = "카카오 로그인/회원가입", description = "카카오 AccessCode를 사용하여 로그인/회원가입을 한다")
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인/회원가입 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginMemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "KA-C-001 유효하지 않은 카카오 코드입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "G-S-001 Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @PostMapping("/member/oauth/kakao")
    public ResponseEntity handleKakaoLogin(@RequestHeader HttpHeaders headers, HttpServletResponse response) {
        String kakaoCode = headers.getFirst("Kakao-Code");

        Optional.ofNullable(kakaoCode)
                .filter(Predicate.not(String::isEmpty))
                .orElseThrow(KakaoInvalidCodeException::new);

        KakaoTokenResponseDto KakaoToken = kakaoService.getKakaoToken(kakaoCode);
        KakaoUserDto userDto = kakaoService.getKakaoUser(KakaoToken);
        LoginMemberResponse loginMemberResponse = memberService.findOrSaveMember(userDto);

        Cookie refreshTokenCookie = new Cookie("refreshToken", loginMemberResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        response.addCookie(refreshTokenCookie);
        response.setHeader("Authorization", "Bearer " + loginMemberResponse.getAccessToken());

        return new ResponseEntity("Login Successful", OK);
    }
}
