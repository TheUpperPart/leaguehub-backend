package leaguehub.leaguehubbackend.service.participant;

import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

import static leaguehub.leaguehubbackend.entity.participant.Role.HOST;
import static leaguehub.leaguehubbackend.entity.participant.Role.OBSERVER;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MemberService memberService;

    public int findParticipantPermission(Long channelId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        if (userDetails == null) {
            return OBSERVER.getNum();
        }

        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        List<Participant> findParticipant = participantRepository.findAllByMemberId(member.getId());

        return findParticipant.stream()
                .filter(participant -> participant.getChannel().getId() == channelId)
                .map(participant -> participant.getRole().getNum())
                .findFirst()
                .orElse(OBSERVER.getNum());
    }

    public String findChannelHost(Long channelId) {
        return participantRepository.findParticipantByRoleAndChannelId(HOST, channelId).getNickname();
    }
}
