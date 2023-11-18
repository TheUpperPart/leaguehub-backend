package leaguehub.leaguehubbackend.service.channel;


import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.channel.ChannelInfoDto;
import leaguehub.leaguehubbackend.entity.channel.ChannelInfo;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelInfoService {

    private final ChannelInfoRepository channelInfoRepository;
    private final ChannelService channelService;


    public ChannelInfoDto getChannelInfoDto(String channelLink) {

        ChannelInfo channelInfo = validateChannelBoard(channelLink);

        return new ChannelInfoDto(channelInfo.getChannelRuleInfo(), channelInfo.getChannelTimeInfo(), channelInfo.getChannelPrizeInfo());
    }


    public ChannelInfo validateChannelBoard(String channelLink) {
        return channelInfoRepository.findChannelInfoByChannel_ChannelLink(channelLink)
                .orElseThrow(() -> new ChannelNotFoundException());
    }
}
