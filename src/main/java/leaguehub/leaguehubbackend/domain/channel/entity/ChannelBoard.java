package leaguehub.leaguehubbackend.domain.channel.entity;

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

    @Column(name = "channel_board_index", nullable = false)
    private int index;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static List<ChannelBoard> createDefaultBoard(Channel channel) {
        List<ChannelBoard> channelBoardList = new ArrayList<>();

        ChannelBoard announcementBoard = new ChannelBoard();
        announcementBoard.title = "리그 공지사항";
        announcementBoard.content = "공지사항을 작성해주세요.";
        announcementBoard.channel = channel;
        announcementBoard.index = 1;

        ChannelBoard ruleBoard = new ChannelBoard();
        ruleBoard.title = "참여자 규칙";
        ruleBoard.content = "참여자 규칙을 작성해주세요.";
        ruleBoard.channel = channel;
        ruleBoard.index = 2;

        ChannelBoard participateBoard = new ChannelBoard();
        participateBoard.title = "참여하기";
        participateBoard.content = "글을 작성해주세요.";
        participateBoard.channel = channel;
        participateBoard.index = 3;

        channelBoardList.add(announcementBoard);
        channelBoardList.add(ruleBoard);
        channelBoardList.add(participateBoard);

        return channelBoardList;
    }


    public static ChannelBoard createChannelBoard(Channel channel,
                                                  String title, String content, int index) {
        ChannelBoard channelBoard = new ChannelBoard();
        channelBoard.channel = channel;
        channelBoard.title = title;
        channelBoard.content = content;
        channelBoard.index = index;

        return channelBoard;
    }

    public ChannelBoard updateChannelBoard(String title, String content) {
        this.title = title;
        this.content = content;

        return this;
    }

    public void updateIndex(int updateIndex) {
        this.index = updateIndex;
    }

    public void deleteChannel() {
        this.channel = null;
    }

}
