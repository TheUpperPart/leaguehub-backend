package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelBoardLoadDto {

    @JsonProperty("boardId")
    private Long id;

    @JsonProperty("boardTitle")
    private String title;

    @JsonProperty("boardIndex")
    private int index;

    public ChannelBoardLoadDto(Long channelBoardId, String title, int index) {
        this.id = channelBoardId;
        this.title = title;
        this.index = index;
    }
}
