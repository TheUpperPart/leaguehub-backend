package leaguehub.leaguehubbackend.domain.channel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateChannelDto {

    @JsonProperty("title")
    @Schema(description = "채널의 제목", example = "채널의 제목입니다.")
    private String title;

    @JsonProperty("maxPlayer")
    @Schema(description = "매치 최대 참가자 수", example = "8, 16, 32, 64")
    private Integer maxPlayer;

    @JsonProperty("channelImageUrl")
    @Schema(description = "채널의 이미지 주소", example = "https://s3.[aws-region].amazonaws.com/[bucket name]")
    private String channelImageUrl;

}
