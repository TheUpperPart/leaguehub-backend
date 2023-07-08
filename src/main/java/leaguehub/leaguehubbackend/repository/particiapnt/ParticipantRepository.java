package leaguehub.leaguehubbackend.repository.particiapnt;

import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findParticipantById(Long id);

    List<Participant> findAllByMemberId(Long memberId);

    Participant findParticipantByRoleAndChannel_ChannelLink(Role role, String channelLink);
}