package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.kakao.KakaoTokenResponseDto;
import leaguehub.leaguehubbackend.dto.kakao.KakaoUserDto;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.kakao.exception.KakaoInvalidCodeException;
import leaguehub.leaguehubbackend.service.jwt.JwtService;
import leaguehub.leaguehubbackend.service.kakao.KakaoService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoController {

    private final KakaoService kakaoService;

    private final MemberService memberService;

    private final JwtService jwtService;

    @GetMapping("/app/login/kakao")
    public ResponseEntity<LoginMemberResponse> handleKakaoLogin(@RequestHeader HttpHeaders headers) {
        String kakaoCode = headers.getFirst("Kakao-Code");

        Optional.ofNullable(kakaoCode)
                .filter(Predicate.not(String::isEmpty))
                .orElseThrow(KakaoInvalidCodeException::new);

        KakaoTokenResponseDto KakaoToken = kakaoService.getKakaoToken(kakaoCode);

        KakaoUserDto userDto = kakaoService.getKakaoUser(KakaoToken);

        memberService.findMemberByPersonalId(String.valueOf(userDto.getId()))
                .orElseGet(() -> memberService.saveMember(userDto).orElseThrow(GlobalServerErrorException::new));

        LoginMemberResponse loginMemberResponse = jwtService.createTokens(String.valueOf(userDto.getId()));

        return ResponseEntity.ok(loginMemberResponse);
    }


}
