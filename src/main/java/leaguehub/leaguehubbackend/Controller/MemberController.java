package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.dto.member.ProfileResponseDto;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Member-Controller", description = "사용자 API")
public class MemberController  {

    private final MemberService memberService;

    @Operation(summary = "사용자 프로필 조회", description = "사용자 id, 이름, 이미지 url 조회")
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer {Access-Token}", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 프로필 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "MB-C-001 존재하지 않는 회원입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @GetMapping("/profile")
    public ProfileResponseDto getProfile() {

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        return memberService.getMemberProfile(userDetails.getUsername());
    }

    @Operation(summary = "앱 로그아웃", description = "앱에서 사용자를 로그아웃")
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer {Access-Token}", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "MB-C-001 존재하지 않는 회원입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @PostMapping("/app/logout")
    public ResponseEntity<String> handleKakaoLogout(HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        memberService.logoutMember(userDetails.getUsername(), userDetails, request, response);

        return ResponseEntity.ok("Logout Success!");
    }

}
