package leaguehub.leaguehubbackend.domain.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ChannelBoardIndexListDto {

    private List<ChannelBoardLoadDto> channelBoardLoadDtoList;

}
