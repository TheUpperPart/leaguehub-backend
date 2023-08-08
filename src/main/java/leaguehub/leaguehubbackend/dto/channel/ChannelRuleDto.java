package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelRuleDto {

    @JsonProperty("tier")
    private Boolean tier;

    @JsonProperty("tierMax")
    private String tierMax;

    @JsonProperty("tierMin")
    private String tierMin;

    @JsonProperty("playCount")
    private Boolean playCount;

    @JsonProperty("playCountMin")
    private Integer playCountMin;

    @Builder
    public ChannelRuleDto(Boolean tier, String tierMax, String tierMin, Boolean playCount, Integer playCountMin) {
        this.tier = tier;
        this.tierMax = tierMax;
        this.tierMin = tierMin;
        this.playCount = playCount;
        this.playCountMin = playCountMin;
    }
}
