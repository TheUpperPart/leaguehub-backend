package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class CreateChannelDto {

    @NotEmpty
    @JsonProperty("category")
    private int game;

    @NotEmpty
    @JsonProperty("matchformat")
    private Integer tournament;

    @NotEmpty
    @JsonProperty("title")
    private String title;

    @NotEmpty
    @JsonProperty("participationNum")
    private Integer participationNum;

    @NotEmpty
    @JsonProperty("tier")
    private Boolean tier;

    @JsonProperty("tierMax")
    private String tierMax;

    @NotBlank
    @JsonProperty("channelImageUrl")
    private String channelImageUrl;

    @NotEmpty
    @JsonProperty("playcount")
    private Boolean playCount;

    @JsonProperty("playcountMin")
    private Integer playCountMin;

    @Builder
    public CreateChannelDto(int game, Integer tournament, String title,
                            Integer participationNum, Boolean tier, String tierMax,
                            Boolean playCount, Integer playCountMin) {
        this.game = game;
        this.tournament = tournament;
        this.title = title;
        this.participationNum = participationNum;
        this.tier = tier;
        this.tierMax = tierMax;
        this.playCount = playCount;
        this.playCountMin = playCountMin;
    }
}
