package leaguehub.leaguehubbackend.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDto {

    private String profileId;

    private String profileImageUrl;

    private String nickName;

}
