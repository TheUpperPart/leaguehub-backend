package leaguehub.leaguehubbackend.entity.channel;


import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class ChannelInfo extends BaseTimeEntity {

    @Id
    @Column(name = "channel_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String channelContentInfo;

    @Column(nullable = false)
    private String channelRuleInfo;

    @Column(nullable = false)
    private String channelTimeInfo;

    @Column(nullable = false)
    private String channelPrizeInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static ChannelInfo createChannelInfo(Channel channel){
        ChannelInfo channelInfo = new ChannelInfo();
        channelInfo.channelContentInfo = "대회의 소제목을 입력해주세요";
        channelInfo.channelTimeInfo = "대회 진행 시간을 입력해주세요";
        channelInfo.channelRuleInfo = "대회 참가 조건을 입력해주세요";
        channelInfo.channelPrizeInfo ="대회 상품 & 상금을 입력해주세요";
        channelInfo.channel = channel;

        return channelInfo;
    }


    public ChannelInfo updateChannelBoard(String channelContentInfo, String channelTimeInfo, String channelRuleInfo, String channelPrizeInfo){
        this.channelContentInfo = channelContentInfo;
        this.channelPrizeInfo = channelPrizeInfo;
        this.channelTimeInfo = channelTimeInfo;
        this.channelRuleInfo = channelRuleInfo;

        return this;
    }



}
