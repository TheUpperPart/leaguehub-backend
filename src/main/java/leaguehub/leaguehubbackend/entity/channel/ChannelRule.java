package leaguehub.leaguehubbackend.entity.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ChannelRule extends BaseTimeEntity {

    @Id
    @Column(name = "channel_rule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer limitedPlayCount;

    private String limitedTier;

    private Boolean tier;

    private Boolean playCount;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static ChannelRule createChannelRule(String tierMax, Boolean tier,
                                                Boolean playCount, Integer playCountMin) {
        ChannelRule channelRule = new ChannelRule();

        channelRule.playCount = playCount;
        channelRule.tier = tier;

        if (tier == true) {
            channelRule.limitedTier = tierMax;
        } else {
            channelRule.limitedTier = GlobalConstant.NO_DATA.getData();
        }

        if (playCount == true) {
            channelRule.limitedPlayCount = playCountMin;
        } else {
            channelRule.limitedPlayCount = Integer.MAX_VALUE;
        }

        return channelRule;
    }
}
