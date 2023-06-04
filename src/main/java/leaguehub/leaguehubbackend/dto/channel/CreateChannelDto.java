package leaguehub.leaguehubbackend.dto.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import leaguehub.leaguehubbackend.entity.channel.Category;
import lombok.Builder;
import lombok.Data;

@Data
public class CreateChannelDto {

    @NotNull
    private int game;

    @NotNull
    private Integer tournament;

    @NotNull
    private String title;

    @NotNull
    private Integer participationNum;

    @NotNull
    private Boolean tier;

    private String tierMax;

    @NotBlank
    private String channelImageUrl;

    @NotNull
    private Boolean playCount;

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
