package leaguehub.leaguehubbackend.entity.channel;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.ChannelCreateException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static leaguehub.leaguehubbackend.entity.participant.Participant.createHostChannel;

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

    private String participationLink;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "channel", cascade = CascadeType.ALL)
    private List<Participant> participant = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "channel", cascade = CascadeType.ALL)
    private List<ChannelBoard> channelBoards = new ArrayList<>();

    //-- 비즈니스 로직 --//

    public static Channel createChannel(CreateChannelDto createChannelDto, Member member) {
        Channel channel = new Channel();
        channel.title = createChannelDto.getTitle();
        channel.category = Category.getByNumber(createChannelDto.getGame());
        channel.maxPlayer = createChannelDto.getParticipationNum();
        channel.realPlayer = 0;
        channel.participant.add(createHostChannel(member, channel));
        channel.channelStatus = ChannelStatus.PREPARING;
        channel.matchFormat = MatchFormat.getByNumber(createChannelDto.getTournament());
        channel.accessCode = createAccessCode();
        channel.channelBoards = ChannelBoard.createDefaultBoard();
        channel.channelImageUrl = channel.validateChannelImageUrl(createChannelDto.getChannelImageUrl());
        channel.channelRule = ChannelRule.createChannelRule(createChannelDto.getTierMax()
                , createChannelDto.getTier()
                , createChannelDto.getPlayCount()
                , createChannelDto.getPlayCountMin());

        validateChannelData(channel);

        return channel;
    }

    private static void validateChannelData(Channel channel) {
        if(channel.getCategory() == null) {
            throw new ChannelCreateException();
        } else if (channel.getMatchFormat() == null) {
            throw new ChannelCreateException();
        }
    }

    private static String createAccessCode() {
        String accessCode = UUID.randomUUID().toString().substring(0, 7);

        return accessCode;
    }

    public static void createParticipationLink(Channel channel) {
        channel.participationLink = "http://localhost:8080/" + channel.getId();
    }

    //채널 이미지 Url에 대한 정보가 없으면 기본 채널 이미지를 반환한다.
    private String validateChannelImageUrl(String channelImageUrl) {
        if (channelImageUrl == null) {
            channelImageUrl = ""; //Default 값
        }

        return channelImageUrl;
    }
}
