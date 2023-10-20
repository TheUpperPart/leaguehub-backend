package leaguehub.leaguehubbackend.dto.channel;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ChannelBoardIndexListDto {

    private List<ChannelBoardLoadDto> channelBoardLoadDtoList;

}
