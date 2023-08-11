package leaguehub.leaguehubbackend.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {

    private String profileImageUrl;

    private String nickName;
}