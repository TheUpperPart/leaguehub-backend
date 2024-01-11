package leaguehub.leaguehubbackend.domain.email.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leaguehub.leaguehubbackend.domain.email.dto.EmailDto;
import leaguehub.leaguehubbackend.domain.email.service.EmailService;
import leaguehub.leaguehubbackend.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Email-Controller", description = "Email 인증")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "인증 메일 보내기", description = "엑세스 토큰이 유효하면 받은 email 주소로 인증 메일을 보낸다")
    @SecurityRequirements
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email Successfully Sent", content = @Content(mediaType = "string", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "MB-C-001 존재하지 않는 회원입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "G-S-001 Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
    })
    @PostMapping("/member/auth/email")
    public ResponseEntity<String> verifyUser(@RequestBody @Valid EmailDto emailDto) {

        String email = emailService.sendEmailWithConfirmation(emailDto.getEmail());

        return ResponseEntity.ok("Email Successfully Sent to " + email);
    }

    @GetMapping("/member/oauth/email")
    public String confirmUserEmail(@RequestParam("token") String token) {
        if (emailService.confirmUserEmail(token)) {
            return "redirect:/mypage";
        } else {
            return "redirect:/";
        }
    }
}
