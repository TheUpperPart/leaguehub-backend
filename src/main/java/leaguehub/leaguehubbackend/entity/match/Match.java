package leaguehub.leaguehubbackend.entity.match;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder
    public Match(MatchStatus matchStatus, Integer matchRound, String matchName, String matchPasswd){
        this.matchStatus = matchStatus;
        this.matchRound = matchRound;
        this.matchName = matchName;
        this.matchPasswd = matchPasswd;
    }
}
