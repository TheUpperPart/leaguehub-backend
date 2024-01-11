package leaguehub.leaguehubbackend.domain.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateChannelBoardDto {
    private Long channelId;

    private Long channelBoardId;

    private String title;

    private String content;
}
