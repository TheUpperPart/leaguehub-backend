package leaguehub.leaguehubbackend.domain.participant.repository;

import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.entity.ParticipantStatus;
import leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus;
import leaguehub.leaguehubbackend.domain.participant.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByMemberId(Long memberId);

    List<Participant> findAllByMemberIdOrderByIndex(Long memberId);

    @Query("select p from Participant p join fetch p.channel where p.channel.channelLink = :channelLink and p.member.id = :memberId")
    Optional<Participant> findParticipantByMemberIdAndChannel_ChannelLink(@Param("memberId") Long memberId, @Param("channelLink") String channelLink);

    @Query("select p from Participant p join fetch p.channel where p.channel.channelLink = :channelLink and p.id = :participantId")
    Optional<Participant> findParticipantByIdAndChannel_ChannelLink(@Param("participantId") Long participantId, @Param("channelLink") String channelLink);

    List<Participant> findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(String channelLink, Role role, RequestStatus requestStatus);

    List<Participant> findAllByChannel_ChannelLink(String channelLink);

    List<Participant> findParticipantByRoleAndChannel_ChannelLinkOrderById(Role role, String channelLink);

    Optional<Participant> findParticipantByMemberIdAndChannel_Id(Long memberId, Long channelId);

    @Query("SELECT MAX(p.index) from Participant p WHERE p.member.id = :memberId")
    Optional<Integer> findMaxIndexByParticipant(@Param("memberId") Long memberId);

    List<Participant> findAllByMemberIdAndAndIndexGreaterThan(Long memberId, int deleteIndex);

    List<Participant> findAllByChannel_ChannelLinkAndRoleAndParticipantStatus(String channelLink, Role role,ParticipantStatus participantStatus);

}