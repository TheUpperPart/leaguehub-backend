package leaguehub.leaguehubbackend.entity.channel;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class ChannelBoard extends BaseTimeEntity {

    @Id
    @Column(name = "channel_board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static List<ChannelBoard> createDefaultBoard() {
        List<ChannelBoard> channelBoardList = new ArrayList<>();

        return channelBoardList;
    }

}
