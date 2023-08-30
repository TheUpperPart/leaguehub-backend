package leaguehub.leaguehubbackend.entity.channel;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@Entity
public class ChannelRule extends BaseTimeEntity {

    @Id
    @Column(name = "channel_rule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer limitedPlayCount;

    private Integer tierMax;

    private Integer tierMin;

    private Boolean tier;

    private Boolean playCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static ChannelRule createChannelRule(Channel channel,Boolean tier, Integer tierMax, Integer tierMin,
                                                Boolean playCount, Integer playCountMin) {
        ChannelRule channelRule = new ChannelRule();

        channelRule.channel = channel;
        channelRule.playCount = playCount;
        channelRule.tier = tier;

        if (tier) {
            channelRule.validateTier(tierMax, tierMin);
            channelRule.tierMax = Optional.ofNullable(tierMax).orElse(Integer.MIN_VALUE);
            channelRule.tierMin = Optional.ofNullable(tierMin).orElse(Integer.MIN_VALUE);
        } else {
            channelRule.tierMax = Integer.MIN_VALUE;
            channelRule.tierMin = Integer.MIN_VALUE;
        }

        if (playCount) {
            channelRule.validatePlayCount(playCountMin);
            channelRule.limitedPlayCount = playCountMin;
        } else {
            channelRule.limitedPlayCount = Integer.MAX_VALUE;
        }

        return channelRule;
    }

    public void updateTierRule(boolean tier, Integer tierMax, Integer tierMin) {
        validateTier(tierMax, tierMin);
        this.tier = tier;
        this.tierMax = Optional.ofNullable(tierMax).orElse(Integer.MIN_VALUE);
        this.tierMin = Optional.ofNullable(tierMin).orElse(Integer.MIN_VALUE);
    }

    public void updatePlayCountMin(boolean playCount, Integer playCountMin) {
        validatePlayCount(playCountMin);
        this.playCount = playCount;
        this.limitedPlayCount = playCountMin;
    }

    public void updateTierRule(boolean tier) {
        this.tier = tier;
    }

    public void updatePlayCountMin(boolean playCount) {
        this.playCount = playCount;
    }

    private void validateTier(Integer tierMax, Integer tierMin) {
        if (tierMax == null && tierMin == null) {
            throw new ChannelRequestException();
        }
    }

    private void validatePlayCount(Integer playCountMin) {
        if (playCountMin == null) {
            throw new ChannelRequestException();
        }
    }
}
