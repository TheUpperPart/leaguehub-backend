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

    @JsonProperty("tier")
    private Boolean tier;

    @JsonProperty("tierMax")
    private String tierMax;

    @JsonProperty("gradeMax")
    private String gradeMax;

    @JsonProperty("channelImageUrl")
    private String channelImageUrl;

    @JsonProperty("playCount")
    private Boolean playCount;

    @JsonProperty("playCountMin")
    private Integer playCountMin;
}
