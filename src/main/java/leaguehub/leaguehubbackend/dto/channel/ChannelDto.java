package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonFormat;
import leaguehub.leaguehubbackend.entity.channel.GameCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelDto {

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private GameCategory gameCategory;

    private Integer realPlayer;

    private Integer maxPlayer;

    @Builder
    public ChannelDto(String title, GameCategory gameCategory, Integer realPlayer, Integer maxPlayer) {
        this.title = title;
        this.gameCategory = gameCategory;
        this.realPlayer = realPlayer;
        this.maxPlayer = maxPlayer;
    }
}
