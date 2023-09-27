package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
public class Match extends BaseTimeEntity {

    @Id
    @Column(name = "match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    private Integer matchRound;

    private String matchName;

    private String matchPasswd;

    private Integer matchSetCount;

    private Integer matchCurrentSet;

    private boolean alarm;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder
    public Match(MatchStatus matchStatus, Integer matchRound, String matchName, String matchPasswd) {
        this.matchStatus = matchStatus;
        this.matchRound = matchRound;
        this.matchName = matchName;
        this.matchPasswd = matchPasswd;
    }

    public static Match createMatch(Integer matchRound, Channel channel, String matchName) {
        Match match = new Match();
        match.matchStatus = MatchStatus.READY;
        match.matchRound = matchRound;
        match.matchName = matchName;
        match.matchPasswd = GlobalConstant.NO_DATA.getData();
        match.matchCurrentSet = 0;
        match.matchSetCount = 3;
        match.channel = channel;
        match.alarm = false;

        return match;
    }

    public void updateMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public void updateMatchSetCount(Integer matchSetCount) { this.matchSetCount = matchSetCount; }

    public void updateCurrentMatchSet(Integer matchCurrentSet) {
        this.matchCurrentSet = matchCurrentSet;
    }

    public void updateCallAlarm(){ this.alarm = true; }

    public void updateOffAlarm(){ this.alarm = false; }


}
