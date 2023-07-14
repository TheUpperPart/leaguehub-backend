package leaguehub.leaguehubbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leaguehub.leaguehubbackend.dto.member.ProfileResponseDto;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile")
    public ProfileResponseDto getProfile() {

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        return memberService.getMemberProfile(userDetails.getUsername());
    }

    @PostMapping("/app/logout")
    public ResponseEntity<String> handleKakaoLogout(HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        memberService.logoutMember(userDetails.getUsername(), userDetails, request, response);

        return ResponseEntity.ok("Logout Success!");
    }
}
