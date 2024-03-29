package leaguehub.leaguehubbackend.domain.channel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class CreateChannelDto {

    @NotNull
    @JsonProperty("gameCategory")
    @Schema(description = "게임 종목(TFT, LOL, FIFA)의 숫자", example = "0, 1, 2")
    private int gameCategory;

    @NotNull
    @JsonProperty("matchFormat")
    @Schema(description = "토너먼트 종류의 숫자", example = "싱글 엘리미네이션(0), 프리 포 올(1)")
    private Integer matchFormat;

    @NotNull
    @JsonProperty("title")
    @Schema(description = "채널의 제목", example = "채널의 제목입니다.")
    private String title;

    @NotNull
    @Min(8)
    @JsonProperty("maxPlayer")
    @Schema(description = "매치 최대 참가자 수", example = "8, 16, 32, 64")
    private Integer maxPlayer;

    @NotNull
    @JsonProperty("tier")
    @Schema(description = "티어 제한의 유무", example = "true, false")
    private Boolean tier;

    @JsonProperty("tierMax")
    @Schema(description = "최대 티어", example = "1200")
    private Integer tierMax;

    @JsonProperty("tierMin")
    @Schema(description = "최소 티어", example = "1600")
    private Integer tierMin;

    @JsonProperty("channelImageUrl")
    @Schema(description = "채널의 이미지 주소", example = "https://s3.[aws-region].amazonaws.com/[bucket name]")
    private String channelImageUrl;

    @NotNull
    @JsonProperty("playCount")
    @Schema(description = "최소 경기 제한의 유무", example = "true, false")
    private Boolean playCount;

    @Min(0)
    @JsonProperty("playCountMin")
    @Schema(description = "최소 경기 수", example = "30, 40, 50")
    private Integer playCountMin;

    @Builder
    public CreateChannelDto(@NotNull int gameCategory, @NotNull Integer matchFormat, @NotNull String title,
                            @NotNull Integer maxPlayer, @NotNull Boolean tier,
                            Integer tierMax, Integer tierMin, String channelImageUrl,
                            @NotNull Boolean playCount, Integer playCountMin) {
        this.gameCategory = gameCategory;
        this.matchFormat = matchFormat;
        this.title = title;
        this.maxPlayer = maxPlayer;
        this.tier = tier;
        this.tierMax = tierMax;
        this.tierMin = tierMin;
        this.channelImageUrl = channelImageUrl;
        this.playCount = playCount;
        this.playCountMin = playCountMin;
    }
}
