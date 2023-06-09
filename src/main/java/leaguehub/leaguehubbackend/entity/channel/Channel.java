package leaguehub.leaguehubbackend.entity.channel;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelCreateException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Channel extends BaseTimeEntity {

    @Id
    @Column(name = "channel_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private Integer maxPlayer;

    private Integer realPlayer;

    @Column(unique = true)
    private String channelLink;

    private String accessCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchFormat matchFormat;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelStatus channelStatus;

    private String channelImageUrl;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_rule_id")
    private ChannelRule channelRule;


    //-- 비즈니스 로직 --//

    public static Channel createChannel(String title, Integer category, int maxPlayer,
                                        Integer matchFormat, String channelImageUrl,
                                        boolean tier, String tierMax, boolean playCount, Integer playCountMin) {
        Channel channel = new Channel();
        String uuid = UUID.randomUUID().toString();
        channel.title = title;
        channel.category = Category.getByNumber(category);
        channel.maxPlayer = maxPlayer;
        channel.realPlayer = 0;
        channel.channelStatus = ChannelStatus.PREPARING;
        channel.matchFormat = MatchFormat.getByNumber(matchFormat);
        channel.channelLink = channel.createParticipationLink(uuid);
        channel.accessCode = channel.createAccessCode(uuid);
        channel.channelImageUrl = channel.validateChannelImageUrl(channelImageUrl);
        channel.channelRule = ChannelRule.createChannelRule(tierMax
                , tier
                , playCount
                , playCountMin);

        channel.validateChannelData();

        return channel;
    }

    private void validateChannelData() {
        if (this.getCategory() == null) {
            throw new ChannelCreateException();
        } else if (this.getMatchFormat() == null) {
            throw new ChannelCreateException();
        }
    }

    private String createAccessCode(String uuid) {
        String accessCode = uuid.substring(0, 8);

        return accessCode;
    }

    public String createParticipationLink(String uuid) {
        String channelLink = uuid.substring(24,uuid.length());

        return channelLink;
    }

    //채널 이미지 Url에 대한 정보가 없으면 기본 채널 이미지를 반환한다.
    private String validateChannelImageUrl(String channelImageUrl) {
        if (channelImageUrl == null) {
            channelImageUrl = ""; //Default 값
        }

        return channelImageUrl;
    }

}
