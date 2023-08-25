package leaguehub.leaguehubbackend.repository.particiapnt;

import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.RequestStatus;
import leaguehub.leaguehubbackend.entity.participant.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByMemberId(Long memberId);

    List<Participant> findAllByMemberIdOrderByIndex(Long memberId);

    @Query("select p from Participant p join fetch p.channel")
    Optional<Participant> findParticipantByMemberIdAndChannel_ChannelLink(Long memberId, String channelLink);

    @Query("select p from Participant p join fetch p.channel")
    Optional<Participant> findParticipantByIdAndChannel_ChannelLink(Long participantId, String channelLink);

    List<Participant> findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(String channelLink, Role role, RequestStatus requestStatus);

    List<Participant> findAllByChannel_ChannelLink(String channelLink);

    Participant findParticipantByRoleAndChannel_ChannelLinkOrderById(Role role, String channelLink);

    Optional<Participant> findParticipantByMemberIdAndChannel_Id(Long memberId, Long channelId);

    @Query("SELECT MAX(p.index) from Participant p WHERE p.member.id = :memberId")
    Optional<Integer> findMaxIndexByParticipant(@Param("memberId") Long memberId);

    List<Participant> findAllByMemberIdAndAndIndexGreaterThan(Long memberId, int deleteIndex);

}