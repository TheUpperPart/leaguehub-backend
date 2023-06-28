package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;

@Data
public class ChannelBoardDto {

    private Long id;

    private String name;

    public ChannelBoardDto(Long channelBoardId, String name) {
        this.id = channelBoardId;
        this.name = name;
    }
}
