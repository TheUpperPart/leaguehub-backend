package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelBoardLoadDto {

    @JsonProperty("boardId")
    private Long id;

    @JsonProperty("boardTitle")
    private String title;

    public ChannelBoardLoadDto(Long channelBoardId, String title) {
        this.id = channelBoardId;
        this.title = title;
    }
}
