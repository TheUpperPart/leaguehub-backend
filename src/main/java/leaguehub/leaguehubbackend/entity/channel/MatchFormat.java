package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MatchFormat {
    SINGLE_ELIMINATION(1), FREE_FOR_ALL(0);

    private int num;

    MatchFormat(int num) {
        this.num = num;
    }


    public static MatchFormat getByNumber(int tournament) {
        return Arrays.stream(MatchFormat.values())
                .filter(matchFormat -> matchFormat.num == tournament)
                .findFirst()
                .orElseThrow(ChannelRequestException::new);
    }
}
