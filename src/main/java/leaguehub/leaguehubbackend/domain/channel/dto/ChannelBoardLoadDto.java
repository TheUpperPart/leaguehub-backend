package leaguehub.leaguehubbackend.domain.channel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelBoardLoadDto {

    @NotBlank
    @JsonProperty("boardId")
    @Schema(description = "게시판 고유 Id", example = "0, 1, 2")
    private Long boardId;

    @NotBlank
    @JsonProperty("boardTitle")
    @Schema(description = "게시판 제목", example = "제목입니다.")
    private String boardTitle;

    @NotBlank
    @JsonProperty("boardIndex")
    @Schema(description = "게시판 위치", example = "0, 1, 2")
    private int boardIndex;

    public ChannelBoardLoadDto(Long channelBoardId, String title, int index) {
        this.boardId = channelBoardId;
        this.boardTitle = title;
        this.boardIndex = index;
    }
}
