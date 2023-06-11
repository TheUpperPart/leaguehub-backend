package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;

@Data
public class UpdateChannelDto {
    private Long channelId;

    private Long channelBoardId;

    private String title;

    private String content;
}
