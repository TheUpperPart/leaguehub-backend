package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.domain.channel.dto.ChannelBoardDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelRuleDto;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.UpdateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;

public class ChannelFixture {

    public static CreateChannelDto createChannelDto() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .gameCategory(0)
                .title("test")
                .matchFormat(0)
                .maxPlayer(16)
                .tier(false)
                .playCount(false)
                .build();


        return createChannelDto;
    }


    public static CreateChannelDto createAllPropertiesCustomChannelDto(Boolean tier, Boolean playCount,
                                                                       Integer tierMax, Integer tierMin, int playCountMin) {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .gameCategory(0)
                .title("test")
                .matchFormat(0)
                .maxPlayer(16)
                .tier(tier)
                .tierMax(tierMax)
                .tierMin(tierMin)
                .playCount(playCount)
                .playCountMin(playCountMin)
                .build();


        return createChannelDto;
    }

    public static CreateChannelDto bindingResultCheck() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .gameCategory(0)
                .matchFormat(0)
                .maxPlayer(16)
                .tier(false)
                .build();


        return createChannelDto;
    }

    public static CreateChannelDto invalidatedCategoryData() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .gameCategory(12)
                .title("test")
                .matchFormat(0)
                .maxPlayer(16)
                .tier(false)
                .playCount(false)
                .build();


        return createChannelDto;
    }

    public static CreateChannelDto invalidatedTournamentData() {
        CreateChannelDto createChannelDto = CreateChannelDto.builder()
                .gameCategory(0)
                .title("test")
                .matchFormat(12)
                .maxPlayer(16)
                .tier(false)
                .playCount(false)
                .build();


        return createChannelDto;
    }

    public static ChannelBoardDto createChannelBoardDto() {
        ChannelBoardDto channelBoardDto = new ChannelBoardDto("test", "test");

        return channelBoardDto;
    }

    public static ChannelBoardDto updateChannelBoardDto() {
        ChannelBoardDto channelBoardDto = new ChannelBoardDto("test1", "test1");

        return channelBoardDto;
    }

    public static Channel createDummyChannel(Boolean tier, Boolean playCount, Integer tierMax, Integer tierMin,int playCountMin){
        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(tier, playCount, tierMax, tierMin,playCountMin);

        return Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
    }

    public static UpdateChannelDto updateChannelDto() {
        UpdateChannelDto updateChannelDto = new UpdateChannelDto();
        updateChannelDto.setTitle("test123");
        updateChannelDto.setChannelImageUrl("test");
        updateChannelDto.setMaxPlayer(64);

        return updateChannelDto;
    }

    public static ChannelRuleDto updateChannelRule() {
        ChannelRuleDto channelRuleDto = new ChannelRuleDto();
        channelRuleDto.setTier(true);
        channelRuleDto.setTierMax(2400);
        channelRuleDto.setTierMin(800);

        channelRuleDto.setPlayCount(true);
        channelRuleDto.setPlayCountMin(100);

        return channelRuleDto;
    }
}
