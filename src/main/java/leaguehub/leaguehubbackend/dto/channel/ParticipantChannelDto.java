package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantChannelDto {

    private String channelLink;

    private String title;

    private Integer category;

    private String imgSrc;

    public ParticipantChannelDto(String channelLink, String title, Integer category, String imgSrc) {
        this.channelLink = channelLink;
        this.title = title;
        this.category = category;
        this.imgSrc = imgSrc;
    }
}
