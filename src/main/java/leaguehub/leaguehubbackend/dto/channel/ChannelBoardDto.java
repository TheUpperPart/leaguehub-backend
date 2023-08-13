package leaguehub.leaguehubbackend.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelBoardDto {

    @Schema(description = "해당 게시판의 제목", example = "제목입니다.")
    private String title;

    @Schema(description = "해당 게시판의 내용", example = "내용입니다.")
    private String content;

    public ChannelBoardDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
