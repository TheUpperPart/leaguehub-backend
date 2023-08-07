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
    @JsonProperty("matchFormat")
    private Integer tournament;

    @NotNull
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("maxPlayer")
    private Integer participationNum;

    @NotNull
    @JsonProperty("tier")
    private Boolean tier;

    @JsonProperty("tierMax")
    private String tierMax;


    @JsonProperty("channelImageUrl")
    private String channelImageUrl;

    @NotNull
    @JsonProperty("playCount")
    private Boolean playCount;

    @JsonProperty("playCountMin")
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
