package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;

public class ChannelFixture {

    public static CreateChannelDto createChannelDto() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .game(0)
                .title("test")
                .tournament(0)
                .participationNum(16)
                .tier(false)
                .playCount(false)
                .build();


        return createChannelDto;
    }

    public static CreateChannelDto bindingResultCheck() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .game(0)
                .tournament(0)
                .participationNum(16)
                .tier(false)
                .build();


        return createChannelDto;
    }
}
