package leaguehub.leaguehubbackend.repository.particiapnt;

import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.RequestStatus;
import leaguehub.leaguehubbackend.entity.participant.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findParticipantById(Long id);

    List<Participant> findAllByMemberId(Long memberId);

    Participant findParticipantByRoleAndChannelId(Role role, Long channelId);

    Optional<Participant> findParticipantByMemberIdAndChannel_ChannelLink(Long memberId, String channelLink);

    Optional<Participant> findParticipantByIdAndChannel_ChannelLink(Long participantId, String channelLink);

    List<Participant> findAllByChannelIdOrderByNicknameAsc(Long channelId);

    List<Participant> findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(String channelLink, Role role, RequestStatus requestStatus);


    List<Participant> findAllByChannel_ChannelLink(String channelLink);

    Participant findParticipantByRoleAndChannel_ChannelLink(Role role, String channelLink);

    Optional<Participant> findParticipantByMemberIdAndChannel_Id(Long memberId, Long channelId);

    @Query("SELECT MAX(p.index) from Participant p WHERE p.member.id = :memberId")
    Integer findMaxIndexByParticipant(Long memberId);
}