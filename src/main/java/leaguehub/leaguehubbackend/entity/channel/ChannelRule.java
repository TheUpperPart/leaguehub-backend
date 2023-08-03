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

    private String limitedGrade;

    private Boolean tier;

    private Boolean playCount;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static ChannelRule createChannelRule(String tierMax, String gradeMax, Boolean tier,
                                                Boolean playCount, Integer playCountMin) {
        ChannelRule channelRule = new ChannelRule();

        channelRule.playCount = playCount;
        channelRule.tier = tier;

        if (tier == true) {
            channelRule.limitedTier = tierMax;
            channelRule.limitedGrade = gradeMax;
        } else {
            channelRule.limitedTier = GlobalConstant.NO_DATA.getData();
            channelRule.limitedGrade = GlobalConstant.NO_DATA.getData();
        }

        if (playCount == true) {
            channelRule.limitedPlayCount = playCountMin;
        } else {
            channelRule.limitedPlayCount = Integer.MAX_VALUE;
        }

        return channelRule;
    }

    public void updateTierRule(boolean tier, String tierMax, String gradeMax) {
        this.tier = tier;
        this.limitedTier = tierMax;
        this.limitedGrade = gradeMax;
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
