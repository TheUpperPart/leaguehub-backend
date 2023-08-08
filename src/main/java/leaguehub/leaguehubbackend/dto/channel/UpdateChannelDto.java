package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateChannelDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("maxPlayer")
    private Integer participationNum;

    @JsonProperty("channelImageUrl")
    private String channelImageUrl;

}
