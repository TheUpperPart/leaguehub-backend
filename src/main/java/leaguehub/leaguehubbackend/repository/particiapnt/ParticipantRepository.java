package leaguehub.leaguehubbackend.repository.particiapnt;

import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.RequestStatus;
import leaguehub.leaguehubbackend.entity.participant.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findParticipantById(Long id);

    List<Participant> findAllByMemberId(Long memberId);

    Participant findParticipantByRoleAndChannelId(Role role, Long channelId);

    Participant findParticipantByMemberIdAndChannel_ChannelLink(Long memberId, String channelLink);

    Participant findParticipantByIdAndChannel_ChannelLink(Long participantId, String channelLink);

    List<Participant> findAllByChannelIdOrderByNicknameAsc(Long channelId);

    List<Participant> findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(String channelLink, Role role, RequestStatus requestStatus);


    List<Participant> findAllByChannel_ChannelLink(String channelLink);

    Participant findParticipantByRoleAndChannel_ChannelLink(Role role, String channelLink);

}