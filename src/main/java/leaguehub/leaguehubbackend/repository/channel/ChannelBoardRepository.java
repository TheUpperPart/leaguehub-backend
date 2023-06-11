package leaguehub.leaguehubbackend.repository.channel;

import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelBoardRepository extends JpaRepository<ChannelBoard, Long> {

    List<ChannelBoard> findAllByChannel(Channel channel);
}