package leaguehub.leaguehubbackend.repository.channel;

import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChannelBoardRepository extends JpaRepository<ChannelBoard, Long> {

    List<ChannelBoard> findAllByChannel_IdOrderByIndex(Long channelId);

    Optional<ChannelBoard> findChannelBoardsByIdAndChannel_Id(Long boardId, Long channelId);

    List<ChannelBoard> findAllByChannelAndIndexGreaterThan(Channel channel, int deleteIndex);

    @Query("SELECT MAX(b.index) FROM ChannelBoard b WHERE b.channel = :channel")
    Integer findMaxIndexByChannel(@Param("channel") Channel channel);
}