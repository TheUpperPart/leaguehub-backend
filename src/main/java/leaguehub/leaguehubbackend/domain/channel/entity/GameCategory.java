package leaguehub.leaguehubbackend.domain.channel.entity;

import leaguehub.leaguehubbackend.domain.channel.exception.exception.ChannelRequestException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GameCategory {
    TFT(0);

    private final int num;

    GameCategory(int num) {
        this.num = num;
    }


    public static GameCategory getByNumber(int game) {
        return Arrays.stream(GameCategory.values())
                .filter(gameCategory -> gameCategory.num == game)
                .findFirst()
                .orElseThrow(ChannelRequestException::new);
    }
}
