package leaguehub.leaguehubbackend.domain.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelBoardDto {

    @NotBlank
    @Schema(description = "해당 게시판의 제목", example = "제목입니다.")
    private String title;

    @NotBlank
    @Schema(description = "해당 게시판의 내용", example = "내용입니다.")
    private String content;

    public ChannelBoardDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
