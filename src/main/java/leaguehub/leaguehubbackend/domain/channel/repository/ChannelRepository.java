package leaguehub.leaguehubbackend.domain.channel.repository;

import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByChannelLink(String channelLink);

}