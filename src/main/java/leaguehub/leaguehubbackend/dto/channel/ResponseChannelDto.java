package leaguehub.leaguehubbackend.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ResponseChannelDto {

    private String hostName;

    private String leagueTitle;

    private String game;

    @JsonProperty("currentPlayer")
    private Integer participateNum;

    @JsonProperty("maxPlayer")
    private Integer maxPlayer;

    private List<ChannelBoardLoadDto> boards;

    private Integer permission;

    @Builder
    public ResponseChannelDto(String hostName, String leagueTitle, String game, Integer participateNum, Integer maxPlayer,List<ChannelBoardLoadDto> channelBoardLoadDtoList, Integer permission) {
        this.hostName = hostName;
        this.leagueTitle = leagueTitle;
        this.game = game;
        this.participateNum = participateNum;
        this.boards = channelBoardLoadDtoList;
        this.permission = permission;
        this.maxPlayer = maxPlayer;
    }
}
