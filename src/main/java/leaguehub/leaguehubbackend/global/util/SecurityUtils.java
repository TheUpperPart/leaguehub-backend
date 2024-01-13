package leaguehub.leaguehubbackend.global.util;

import leaguehub.leaguehubbackend.domain.member.exception.member.exception.MemberNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    public static UserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new MemberNotFoundException();
        }

        return (UserDetails) authentication.getPrincipal();
    }
}
