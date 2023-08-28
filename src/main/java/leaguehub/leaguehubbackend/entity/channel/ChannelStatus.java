package leaguehub.leaguehubbackend.entity.channel;


import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;

import java.util.Arrays;

public enum ChannelStatus {
    PREPARING(0), PROCEEDING(1), FINISH(2);

    private final Integer status;

    ChannelStatus(Integer status) {
        this.status = status;
    }

    public static ChannelStatus convertStatus(Integer status) {
        return Arrays.stream(ChannelStatus.values())
                .filter(channelStatus -> (channelStatus.status == status))
                .findFirst()
                .orElseThrow(ChannelRequestException::new);
    }
}
