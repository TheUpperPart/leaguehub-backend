package leaguehub.leaguehubbackend.domain.channel.repository;

import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRuleRepository extends JpaRepository<ChannelRule, Long> {

    ChannelRule findChannelRuleByChannel_Id(Long channelId);

    ChannelRule findChannelRuleByChannel_ChannelLink(String channelLink);
}