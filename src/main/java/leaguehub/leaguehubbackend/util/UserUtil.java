package leaguehub.leaguehubbackend.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static String getUserPersonalId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
