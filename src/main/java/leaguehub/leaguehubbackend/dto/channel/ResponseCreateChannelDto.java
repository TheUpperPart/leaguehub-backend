package leaguehub.leaguehubbackend.dto.channel;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResponseCreateChannelDto {

    private String channelLink;

    public ResponseCreateChannelDto(String channelLink) {
        this.channelLink = channelLink;
    }
}
