package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class CreateChannelDto {

    @NotEmpty
    @JsonProperty("category")
    private int game;

    @NotEmpty
    @JsonProperty("matchformat")
    private Integer tournament;

    @NotEmpty
    private String title;

    @NotEmpty
    private Integer participationNum;

    @NotEmpty
    private Boolean tier;

    private String tierMax;

    @NotBlank
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
