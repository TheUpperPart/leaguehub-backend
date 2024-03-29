package leaguehub.leaguehubbackend.domain.channel.repository;

import leaguehub.leaguehubbackend.domain.channel.entity.ChannelInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChannelInfoRepository extends JpaRepository<ChannelInfo, Long> {

    @Query("select c from ChannelInfo c join fetch c.channel where c.channel.channelLink = :channelLink")
    Optional<ChannelInfo> findChannelInfoByChannel_ChannelLink(@Param("channelLink") String channelLink);
}
