package leaguehub.leaguehubbackend.entity.channel;

import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Category {
    TFT(0);

    private final int num;

    Category(int num) {
        this.num = num;
    }


    public static Category getByNumber(int game) {
        return Arrays.stream(Category.values())
                .filter(category -> category.num == game)
                .findFirst()
                .orElseThrow(ChannelRequestException::new);
    }
}
