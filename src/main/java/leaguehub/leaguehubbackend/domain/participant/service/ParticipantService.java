package leaguehub.leaguehubbackend.domain.participant.service;

import leaguehub.leaguehubbackend.domain.email.exception.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.domain.member.entity.BaseRole;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.entity.Role;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantNotFoundException;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.USER;


@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MemberService memberService;


    /**
     * 제 3자가 channelLink와 participantId로 해당 채널에서의 참가자 찾기
     * @param channelLink
     * @param participantId
     * @return
     */
    public Participant getFindParticipant(String channelLink, Long participantId) {
        Participant findParticipant = participantRepository.findParticipantByIdAndChannel_ChannelLink(participantId, channelLink)
                .orElseThrow(ParticipantNotFoundException::new);
        return findParticipant;
    }


    /**
     * 자기 자신이 participant를 찾을 때
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


    /**
     * 해당 채널의 역할이 맞는지 확인
     * @param myRole
     * @param checkRole
     */
    public void checkRole(Role myRole, Role checkRole) {
        if (myRole != checkRole) {
            throw new InvalidParticipantAuthException();
        }
    }

    /**
     * 이메일이 인증되었는지 확인
     *
     * @param baseRole
     */
    private void checkEmail(BaseRole baseRole) {
        if (baseRole != USER) throw new UnauthorizedEmailException();
    }


}
