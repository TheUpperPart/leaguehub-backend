package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;

@Data
public class ChannelBoardLoadDto {

    private Long id;

    private String title;

    public ChannelBoardLoadDto(Long channelBoardId, String title) {
        this.id = channelBoardId;
        this.title = title;
    }
}
