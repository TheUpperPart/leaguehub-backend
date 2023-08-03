package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.dto.email.EmailDto;
import leaguehub.leaguehubbackend.service.email.EmailService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/api/member/verify/email")
    public ResponseEntity<String> verifyUser(@RequestBody EmailDto emailDto) {

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        emailService.sendEmailWithConfirmation(emailDto.getEmail(), userDetails);

        return ResponseEntity.ok("Email Sent Success");
    }

    @GetMapping("/confirm/Email")
    public String confirmUserEmail(@RequestParam("token") String token) {
        if (emailService.confirmUserEmail(token)) {
            return "redirect:/verifiedPage.html";
        } else {
            return "redirect:/invalidPage.html";
        }
    }
}
