package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;

@Data
public class ChannelBoardDto {

    private String title;

    private String content;

    public ChannelBoardDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
