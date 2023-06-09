package leaguehub.leaguehubbackend.util;

import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
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
