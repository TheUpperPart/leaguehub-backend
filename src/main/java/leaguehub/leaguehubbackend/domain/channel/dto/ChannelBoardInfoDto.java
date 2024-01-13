package leaguehub.leaguehubbackend.domain.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ChannelBoardInfoDto {

    @Schema(description = "진행중인 매치 라운드", example = "1, 2, 3 없으면 0")
    Integer myMatchRound;

    @Schema(description = "진행중인 매치 PK", example = "1, 2, 3 없으면 0")
    Long myMatchId;

    @Schema(description = "게시판 정보들")
    List<ChannelBoardLoadDto> channelBoardLoadDtoList;

    public ChannelBoardInfoDto(Integer myMatchRound, Long myMatchId, List<ChannelBoardLoadDto> channelBoardLoadDtoList) {
        this.myMatchRound = myMatchRound;
        this.myMatchId = myMatchId;
        this.channelBoardLoadDtoList = channelBoardLoadDtoList;
    }
}
