package leaguehub.leaguehubbackend.domain.channel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelRuleDto {

    @NotNull
    @JsonProperty("tier")
    @Schema(description = "티어 제한의 유무", example = "true, false")
    private Boolean tier;

    @JsonProperty("tierMax")
    @Schema(description = "최대 티어", example = "platinum III일 경우 12000")
    private Integer tierMax;

    @JsonProperty("tierMin")
    @Schema(description = "최소 티어", example = "bronze II 일경우 600")
    private Integer tierMin;

    @NotNull
    @JsonProperty("playCount")
    @Schema(description = "최소 경기 제한의 유무", example = "true, false")
    private Boolean playCount;

    @Min(0)
    @JsonProperty("playCountMin")
    @Schema(description = "최소 경기 수", example = "30, 40, 50")
    private Integer playCountMin;

    @Builder
    public ChannelRuleDto(Boolean tier, Integer tierMax, Integer tierMin, Boolean playCount, Integer playCountMin) {
        this.tier = tier;
        this.tierMax = tierMax;
        this.tierMin = tierMin;
        this.playCount = playCount;
        this.playCountMin = playCountMin;
    }
}
