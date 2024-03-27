package leaguehub.leaguehubbackend.domain.participant.service;


import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantRealPlayerIsMaxException;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.GUEST;
import static leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus.DONE;
import static leaguehub.leaguehubbackend.domain.participant.entity.Role.HOST;
import static leaguehub.leaguehubbackend.domain.participant.entity.Role.PLAYER;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantRoleAndPermissionService {


    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;

    /**
     * 해당 채널의 요청한 참가자를 승인해줌
     *
     * @param channelLink
     * @param participantId
     */
    public void approveParticipantRequest(String channelLink, Long participantId) {
        Participant participant = participantService.getParticipant(channelLink);
        participantService.checkRole(participant.getRole(), HOST);

        checkRealPlayerCount(participant.getChannel());

        Participant findParticipant = participantService.getFindParticipant(channelLink, participantId);

        findParticipant.approveParticipantMatch();

        updateRealPlayerCount(channelLink, participant.getChannel());
    }


    /**
     * 해당 채널의 요청한 참가자를 거절함
     *
     * @param channelLink
     * @param participantId
     */
    public void rejectedParticipantRequest(String channelLink, Long participantId) {
        Participant participant = participantService.getParticipant(channelLink);
        participantService.checkRole(participant.getRole(), HOST);


        Participant findParticipant = participantService.getFindParticipant(channelLink, participantId);

        findParticipant.rejectParticipantRequest();

        updateRealPlayerCount(channelLink, participant.getChannel());
    }


    /**
     * 사용자를 관리자로 권한을 변경한다.
     *
     * @param channelLink
     * @param participantId
     */
    public void updateHostRole(String channelLink, Long participantId) {
        Participant findParticipant = checkHostAndGetParticipant(channelLink, participantId);
        if (findParticipant.getMember().getBaseRole() == GUEST)
            throw new InvalidParticipantAuthException();

        findParticipant.updateHostRole();
    }


    /**
     * 해당 사용자가 호스트인지 확인
     * @param channelLink
     */
    public void checkAdminHost(String channelLink) {
        Participant participant = participantService.getParticipant(channelLink);
        participantService.checkRole(participant.getRole(), HOST);
    }



    /**
     * 해당 채널의 경기 참여자 횟수 체크
     * @param channel
     */
    private void checkRealPlayerCount(Channel channel) {
        if (channel.getRealPlayer() >= channel.getMaxPlayer())
            throw new ParticipantRealPlayerIsMaxException();
    }


    /**
     * 참여된 참가자 수 업데이트
     * @param channelLink
     * @param channel
     */
    private void updateRealPlayerCount(String channelLink, Channel channel) {
        List<Participant> playerLists = participantRepository
                .findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, PLAYER, DONE);

        channel.updateRealPlayer(playerLists.size());
    }

    /**
     * 호스트 확인 및 그 사용자를 반환하는 메서드
     * @param channelLink
     * @param participantId
     * @return
     */
    private Participant checkHostAndGetParticipant(String channelLink, Long participantId) {
        Participant participant = participantService.getParticipant(channelLink);
        participantService.checkRole(participant.getRole(), HOST);

        return participantService.getFindParticipant(channelLink, participantId);
    }

}
