package leaguehub.leaguehubbackend.repository.channel;

import leaguehub.leaguehubbackend.entity.channel.ChannelInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelInfoRepository extends JpaRepository<ChannelInfo, Long> {

    Optional<ChannelInfo> findChannelInfoByChannel_ChannelLink(String channelLink);
}
