package leaguehub.leaguehubbackend.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    GUEST("ROLE_GUEST");
    private final String key;

    public String convertBaseRole() {
        return "[" + this.key + "]";
    }
}
