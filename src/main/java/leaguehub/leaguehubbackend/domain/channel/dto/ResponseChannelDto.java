package leaguehub.leaguehubbackend.domain.channel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseChannelDto {

    private String hostName;

    private String leagueTitle;

    private Integer gameCategory;

    @JsonProperty("currentPlayer")
    private Integer participateNum;

    @JsonProperty("maxPlayer")
    private Integer maxPlayer;

    private Integer permission;

    @Builder
    public ResponseChannelDto(String hostName, String leagueTitle, Integer gameCategory, Integer participateNum, Integer maxPlayer, Integer permission) {
        this.hostName = hostName;
        this.leagueTitle = leagueTitle;
        this.gameCategory = gameCategory;
        this.participateNum = participateNum;
        this.permission = permission;
        this.maxPlayer = maxPlayer;
    }
}
