package leaguehub.leaguehubbackend.domain.participant.service;

import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
import leaguehub.leaguehubbackend.domain.email.exception.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.domain.member.entity.BaseRole;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.entity.Role;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantNotFoundException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantRealPlayerIsMaxException;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.GUEST;
import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.USER;
import static leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus.DONE;
import static leaguehub.leaguehubbackend.domain.participant.entity.Role.HOST;
import static leaguehub.leaguehubbackend.domain.participant.entity.Role.PLAYER;


@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MemberService memberService;
    private final ChannelService channelService;
    @Value("${riot-api-key-1}")
    private String riot_api_key;


    //관리자가 요청 참가자 승인 서비스 -> 커맨드

    /**
     * 해당 채널의 요청한 참가자를 승인해줌
     *
     * @param channelLink
     * @param participantId
     */
    public void approveParticipantRequest(String channelLink, Long participantId) {
        Participant participant = getParticipant(channelLink);
        checkRole(participant.getRole(), HOST);

        checkRealPlayerCount(participant.getChannel());

        Participant findParticipant = getFindParticipant(channelLink, participantId);

        findParticipant.approveParticipantMatch();

        updateRealPlayerCount(channelLink, participant.getChannel());
    }


    //관리자가 요청 참가자 거절 서비스 -> 커맨드

    /**
     * 해당 채널의 요청한 참가자를 거절함
     *
     * @param channelLink
     * @param participantId
     */
    public void rejectedParticipantRequest(String channelLink, Long participantId) {
        Participant participant = getParticipant(channelLink);
        checkRole(participant.getRole(), HOST);


        Participant findParticipant = getFindParticipant(channelLink, participantId);

        findParticipant.rejectParticipantRequest();

        updateRealPlayerCount(channelLink, participant.getChannel());
    }


    //관리자가 관전자를 관리자로 권한 변경 서비스 -> 커맨드

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


    //해당 사용자가 호스트인지 확인 -> 커맨드
    public void checkAdminHost(String channelLink) {
        Participant participant = getParticipant(channelLink);
        checkRole(participant.getRole(), HOST);
    }


    //해당 채널의 경기 참여자 횟수 체크 - 커맨드
    private void checkRealPlayerCount(Channel channel) {
        if (channel.getRealPlayer() >= channel.getMaxPlayer())
            throw new ParticipantRealPlayerIsMaxException();
    }


    /**
     * 이메일이 인증되었는지 확인
     *
     * @param baseRole
     */
    public void checkEmail(BaseRole baseRole) {
        if (baseRole != USER) throw new UnauthorizedEmailException();
    }


    //참여 채널의 순서를 커스텀 - 커맨드
    public List<ParticipantChannelDto> updateCustomChannelIndex(List<ParticipantChannelDto> participantChannelDtoList) {
        Member member = memberService.findCurrentMember();

        List<Participant> allByMemberId = participantRepository.findAllByMemberId(member.getId());

        participantChannelDtoList.forEach(participantChannelDto -> {
            allByMemberId.stream()
                    .filter(participant -> participant.getChannel().getChannelLink().equals(participantChannelDto.getChannelLink()))
                    .forEach(participant -> participant.updateCustomChannelIndex(participantChannelDto.getCustomChannelIndex()));
        });

        return channelService.findParticipantChannelList();
    }


    /**
     * channelLink와 member로 해당 채널에서의 참가자 찾기 -> 쿼리
     *
     * @param channelLink
     * @param member
     * @return
     */
    public Participant getParticipant(String channelLink, Member member) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink)
                .orElseThrow(ParticipantNotFoundException::new);
        return participant;
    }


    /**
     * 제 3자가 channelLink와 participantId로 해당 채널에서의 참가자 찾기 --> 쿼리
     *
     * @param channelLink
     * @param participantId
     * @return
     */
    public Participant getFindParticipant(String channelLink, Long participantId) {
        Participant findParticipant = participantRepository.findParticipantByIdAndChannel_ChannelLink(participantId, channelLink)
                .orElseThrow(ParticipantNotFoundException::new);
        return findParticipant;
    }

    //현재 채널의 경기 참여자 수 업데이트 -> 커맨드
    private void updateRealPlayerCount(String channelLink, Channel channel) {
        List<Participant> playerLists = participantRepository
                .findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, PLAYER, DONE);

        channel.updateRealPlayer(playerLists.size());
    }

    //호스트 확인 및 그 사용자를 가져오는 서비스 -> 쿼리
    private Participant checkHostAndGetParticipant(String channelLink, Long participantId) {
        Participant participant = getParticipant(channelLink);
        checkRole(participant.getRole(), HOST);

        return getFindParticipant(channelLink, participantId);
    }


    /**
     * 자기 자신이 participant를 찾을 때
     *
     * @param channelLink
     * @return
     */
    public Participant getParticipant(String channelLink) {

        Member member = memberService.findCurrentMember();
        checkEmail(member.getBaseRole());

        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink)
                .orElseThrow(() -> new InvalidParticipantAuthException());

        return participant;
    }


    //해당 채널의 Rule이 맞는지 확인 -> 여기에 남는다
    public void checkRole(Role myRole, Role checkRole) {
        if (myRole != checkRole) {
            throw new InvalidParticipantAuthException();
        }
    }

}
