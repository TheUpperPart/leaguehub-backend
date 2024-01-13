package leaguehub.leaguehubbackend.domain.channel.repository;

import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChannelBoardRepository extends JpaRepository<ChannelBoard, Long> {

    List<ChannelBoard> findAllByChannel_ChannelLinkOrderByIndex(String channelLink);

    Optional<ChannelBoard> findChannelBoardsByIdAndChannel_ChannelLink(Long boardId, String channelLink);

    Optional<ChannelBoard> findChannelBoardsByIdAndChannel_Id(Long boardId, Long channelId);

    List<ChannelBoard> findAllByChannel_IdOrderByIndex(Long channelId);

    List<ChannelBoard> findAllByChannelAndIndexGreaterThan(Channel channel, int deleteIndex);

    List<ChannelBoard> findChannelBoardsByChannel_ChannelLink(String channelLink);

    @Query("SELECT MAX(b.index) FROM ChannelBoard b WHERE b.channel = :channel")
    Integer findMaxIndexByChannel(@Param("channel") Channel channel);
}