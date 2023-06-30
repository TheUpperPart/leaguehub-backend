package leaguehub.leaguehubbackend.repository.channel;

import leaguehub.leaguehubbackend.entity.channel.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}