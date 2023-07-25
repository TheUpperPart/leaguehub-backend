package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;

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


    public static CreateChannelDto createAllPropertiesCustomChannelDto(Boolean tier, Boolean playCount,
                                                                       String tierMax, String gradeMax, int playCountMin) {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .game(0)
                .title("test")
                .tournament(0)
                .participationNum(16)
                .tier(tier)
                .tierMax(tierMax)
                .gradeMax(gradeMax)
                .playCount(playCount)
                .playCountMin(playCountMin)
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

    public static ChannelBoardDto createChannelBoardDto() {
        ChannelBoardDto channelBoardDto = new ChannelBoardDto("test", "test");

        return channelBoardDto;
    }

    public static ChannelBoardDto updateChannelDto() {
        ChannelBoardDto channelBoardDto = new ChannelBoardDto("test1", "test1");

        return channelBoardDto;
    }
}
