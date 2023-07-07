package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.member.ProfileResponseDto;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
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
        System.out.println(memberService.getMemberProfile(userDetails.getUsername()));
        return memberService.getMemberProfile(userDetails.getUsername());
    }
}
