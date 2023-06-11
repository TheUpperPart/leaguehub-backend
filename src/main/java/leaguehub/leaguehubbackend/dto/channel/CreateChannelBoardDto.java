package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;

@Data
public class CreateChannelBoardDto {

    private Long channelId;

    private String title;

    private String content;
}
