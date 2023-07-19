package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.RequestChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.UpdateChannelBoardDto;

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

    public static CreateChannelDto createAllPropertiesChannelDto() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .game(0)
                .title("test")
                .tournament(0)
                .participationNum(16)
                .tier(true)
                .tierMax("Sliver")
                .playCount(true)
                .playCountMin(100)
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

    public static CreateChannelDto invalidatedCategoryData() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .game(12)
                .title("test")
                .tournament(0)
                .participationNum(16)
                .tier(false)
                .playCount(false)
                .build();


        return createChannelDto;
    }

    public static CreateChannelDto invalidatedTournamentData() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .game(0)
                .title("test")
                .tournament(12)
                .participationNum(16)
                .tier(false)
                .playCount(false)
                .build();


        return createChannelDto;
    }

    public static RequestChannelBoardDto createChannelBoardDto() {
        RequestChannelBoardDto channelBoardDto = new RequestChannelBoardDto();
        channelBoardDto.setTitle("test");
        channelBoardDto.setContent("test");

        return channelBoardDto;
    }

    public static UpdateChannelBoardDto updateChannelDto() {
        UpdateChannelBoardDto channelBoardDto = new UpdateChannelBoardDto();
        channelBoardDto.setTitle("test1");
        channelBoardDto.setContent("test1");

        return channelBoardDto;
    }
}
