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

    @NotNull
    @JsonProperty("category")
    private int game;

    @NotNull
    @JsonProperty("matchformat")
    private Integer tournament;

    @NotNull
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("participationNum")
    private Integer participationNum;

    @NotNull
    @JsonProperty("tier")
    private Boolean tier;

    @JsonProperty("tierMax")
    private String tierMax;

    @JsonProperty("gradeMax")
    private String gradeMax;

    @JsonProperty("channelImageUrl")
    private String channelImageUrl;

    @NotNull
    @JsonProperty("playcount")
    private Boolean playCount;

    @JsonProperty("playcountMin")
    private Integer playCountMin;

    @Builder
    public CreateChannelDto(int game, Integer tournament, String title,
                            Integer participationNum, Boolean tier, String tierMax, String gradeMax,
                            Boolean playCount, Integer playCountMin) {
        this.game = game;
        this.tournament = tournament;
        this.title = title;
        this.participationNum = participationNum;
        this.tier = tier;
        this.tierMax = tierMax;
        this.gradeMax = gradeMax;
        this.playCount = playCount;
        this.playCountMin = playCountMin;
    }
}
