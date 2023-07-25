package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonFormat;
import leaguehub.leaguehubbackend.entity.channel.Category;
import lombok.Builder;
import lombok.Data;

@Data
public class ChannelDto {

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Category category;

    private Integer realPlayer;

    private Integer maxPlayer;

    @Builder
    public ChannelDto(String title, Category category, Integer realPlayer, Integer maxPlayer) {
        this.title = title;
        this.category = category;
        this.realPlayer = realPlayer;
        this.maxPlayer = maxPlayer;
    }
}
