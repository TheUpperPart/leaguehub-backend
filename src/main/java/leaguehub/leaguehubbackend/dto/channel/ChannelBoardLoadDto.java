package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelBoardLoadDto {

    @JsonProperty("boardId")
    private Long boardId;

    @JsonProperty("boardTitle")
    private String boardTitle;

    @JsonProperty("boardIndex")
    private int boardIndex;

    public ChannelBoardLoadDto(Long channelBoardId, String title, int index) {
        this.boardId = channelBoardId;
        this.boardTitle = title;
        this.boardIndex = index;
    }
}
