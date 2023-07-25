package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseCreateChannelDto {

    private String channelLink;

    public ResponseCreateChannelDto(String channelLink) {
        this.channelLink = channelLink;
    }
}
