package leaguehub.leaguehubbackend.entity.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
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

    private String tierMax;

    private String tierMin;

    private Boolean tier;

    private Boolean playCount;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static ChannelRule createChannelRule(Channel channel,Boolean tier, String tierMax, String tierMin,
                                                Boolean playCount, Integer playCountMin) {
        ChannelRule channelRule = new ChannelRule();

        channelRule.channel = channel;
        channelRule.playCount = playCount;
        channelRule.tier = tier;

        if (tier) {
            channelRule.tierMax = Optional.ofNullable(tierMax).orElse(GlobalConstant.NO_DATA.getData());
            channelRule.tierMin = Optional.ofNullable(tierMin).orElse(GlobalConstant.NO_DATA.getData());
        } else {
            channelRule.tierMax = GlobalConstant.NO_DATA.getData();
            channelRule.tierMin = GlobalConstant.NO_DATA.getData();
        }

        if (playCount) {
            channelRule.limitedPlayCount = playCountMin;
        } else {
            channelRule.limitedPlayCount = Integer.MAX_VALUE;
        }

        return channelRule;
    }

    public void updateTierRule(boolean tier, String tierMax, String tierMin) {
        this.tier = tier;

        this.tierMax = Optional.ofNullable(tierMax).orElse(GlobalConstant.NO_DATA.getData());
        this.tierMin = Optional.ofNullable(tierMin).orElse(GlobalConstant.NO_DATA.getData());
    }

    public void updatePlayCountMin(boolean playCount, Integer playCountMin) {
        this.playCount = playCount;
        this.limitedPlayCount = playCountMin;
    }

    public void updateTierRule(boolean tier) {
        this.tier = tier;
    }

    public void updatePlayCountMin(boolean playCount) {
        this.playCount = playCount;
    }
}
