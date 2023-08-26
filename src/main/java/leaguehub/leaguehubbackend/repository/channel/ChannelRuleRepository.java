package leaguehub.leaguehubbackend.repository.channel;

import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRuleRepository extends JpaRepository<ChannelRule, Long> {

    ChannelRule findChannelRuleByChannel_Id(Long channelId);

    ChannelRule findChannelRuleByChannel_ChannelLink(String channelLink);
}