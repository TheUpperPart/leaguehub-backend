package leaguehub.leaguehubbackend.domain.channel.entity;

import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelRequestException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MatchFormat {
    SINGLE_ELIMINATION(1), FREE_FOR_ALL(0);

    private final int num;

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
