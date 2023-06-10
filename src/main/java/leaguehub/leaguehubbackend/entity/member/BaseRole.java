package leaguehub.leaguehubbackend.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    private final String key;

}
