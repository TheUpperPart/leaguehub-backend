package leaguehub.leaguehubbackend.entity.channel;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import static java.util.UUID.randomUUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Channel extends BaseTimeEntity {

    @Value("${cloud.aws.s3.bucket.url}")
    @Transient
    private String defaultUrl;

    @Id
    @Column(name = "channel_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameCategory gameCategory;

    @Column(nullable = false)
    private Integer maxPlayer;

    private Integer realPlayer;

    @Column(unique = true)
    private String channelLink;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchFormat matchFormat;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelStatus channelStatus;

    private String channelImageUrl;

    //-- 비즈니스 로직 --//
    public static Channel createChannel(String title, int game, int maxPlayer,
                                        int matchFormat, String channelImageUrl) {
        Channel channel = new Channel();
        String uuid = randomUUID().toString();
        channel.title = title;
        channel.gameCategory = GameCategory.getByNumber(game);
        channel.maxPlayer = maxPlayer;
        channel.realPlayer = 0;
        channel.channelStatus = ChannelStatus.PREPARING;
        channel.matchFormat = MatchFormat.getByNumber(matchFormat);
        channel.channelLink = channel.createParticipationLink(uuid);
        channel.channelImageUrl = channel.validateChannelImageUrl(channelImageUrl);

        return channel;
    }

    public String createParticipationLink(String uuid) {
        String channelLink = uuid.substring(24, uuid.length());

        return channelLink;
    }

    //채널 이미지 Url에 대한 정보가 없으면 기본 채널 이미지를 반환한다.
    private String validateChannelImageUrl(String channelImageUrl) {
        if (channelImageUrl == null) {
            channelImageUrl = null; //Default 값
        }

        return channelImageUrl;
    }

    //실제 참가자 수를 업데이트 한다.
    public Channel updateRealPlayer(Integer realPlayer) {
        this.realPlayer = realPlayer;

        return this;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateMaxPlayer(Integer maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public void updateChannelImageUrl(String channelImageUrl) {
        this.channelImageUrl = channelImageUrl;
    }

    public void updateChannelStatus(ChannelStatus channelStatus) {
        this.channelStatus = channelStatus;
    }
}
