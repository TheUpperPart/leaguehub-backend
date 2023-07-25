package leaguehub.leaguehubbackend.dto.channel;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ResponseChannelDto {

    private String hostName;

    private String leagueTitle;

    private String game;

    private Integer participateNum;

    private List<ChannelBoardLoadDto> boards;

    private Integer permission;

    @Builder
    public ResponseChannelDto(String hostName, String leagueTitle, String game, Integer participateNum, List<ChannelBoardLoadDto> channelBoardLoadDtoList, Integer permission) {
        this.hostName = hostName;
        this.leagueTitle = leagueTitle;
        this.game = game;
        this.participateNum = participateNum;
        this.boards = channelBoardLoadDtoList;
        this.permission = permission;
    }
}
