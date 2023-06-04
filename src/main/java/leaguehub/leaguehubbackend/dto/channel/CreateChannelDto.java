package leaguehub.leaguehubbackend.dto.channel;

import jakarta.validation.constraints.NotNull;
import leaguehub.leaguehubbackend.entity.channel.Category;
import lombok.Data;

@Data
public class CreateChannelDto {

    @NotNull
    private Category game;

    @NotNull
    private Integer tournament;

    @NotNull
    private String title;

    @NotNull
    private Integer participationNum;

    private Boolean tier;

    private String tierMax;

    private String channelImageUrl;

    private Boolean playCount;

    private Integer playCountMin;
}
