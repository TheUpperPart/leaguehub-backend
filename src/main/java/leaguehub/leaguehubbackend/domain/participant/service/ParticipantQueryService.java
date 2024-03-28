package leaguehub.leaguehubbackend.domain.participant.service;

import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.dto.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.USER;
import static leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus.*;
import static leaguehub.leaguehubbackend.domain.participant.entity.Role.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantQueryService {


    private final MemberService memberService;
    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;
    private final ParticipantWebClientService participantWebClientService;


    /**
     * 참여자의 권한 확인 메소드
     *
     * @param channelLink
     * @return
     */
    public int findParticipantPermission(String channelLink) {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        if (userDetails == null) return OBSERVER.getNum();
        Member member = memberService.validateMember(userDetails.getUsername());


        Optional<Participant> findParticipant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink);

        return findParticipant.map(participant -> participant.getRole().getNum())
                .orElse(OBSERVER.getNum());
    }


    /**
     * 해당 채널의 첫 번째 관리자 반환 메소드
     *
     * @param channelLink
     * @return
     */
    public String findChannelHost(String channelLink) {
        return participantRepository.findParticipantByRoleAndChannel_ChannelLinkOrderById(HOST, channelLink).get(0).getNickname();
    }


    /**
     * 해당 채널의 관전자인 유저들을 조회
     *
     * @param channelLink
     * @return
     */
    public List<ResponseStatusPlayerDto> loadObserverPlayerList(String channelLink) {
        Participant findParticipant = participantService.getParticipant(channelLink);

        participantService.checkRole(findParticipant.getRole(), HOST);

        List<Participant> findParticipants = participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, OBSERVER, NO_REQUEST);

        return findParticipants.stream()
                .filter(participant -> participant.getMember().getBaseRole() == USER)
                .map(participant -> mapToResponseStatusPlayerDto(participant))
                .collect(Collectors.toList());
    }

    /**
     * 요청을 보낸 사람들을 조회
     *
     * @param channelLink
     * @return
     */
    public List<ResponseStatusPlayerDto> loadRequestStatusPlayerList(String channelLink) {
        Participant findParticipant = participantService.getParticipant(channelLink);
        participantService.checkRole(findParticipant.getRole(), HOST);

        List<Participant> findParticipants =
                participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc
                        (channelLink, OBSERVER, REQUEST);

        return findParticipants.stream()
                .map(participant -> mapToResponseStatusPlayerDto(participant))
                .collect(Collectors.toList());
    }

    /**
     * 해당 채널의 PLAYER 역할인 유저들을 반환
     *
     * @param channelLink
     * @return RequestPlayerDtoList
     */
    public List<ResponseStatusPlayerDto> loadPlayers(String channelLink) {

        return participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc
                        (channelLink, PLAYER, DONE)
                .stream()
                .map(participant -> mapToResponseStatusPlayerDto(participant))
                .collect(Collectors.toList());
    }

    /**
     * 게임 카테고리에 따라 요청 분할
     *
     * @param gameId
     * @param category
     * @return
     */
    public ResponseUserGameInfoDto selectGameCategory(String gameId, Integer category) {
        ResponseUserGameInfoDto userGameInfoDto = new ResponseUserGameInfoDto();

        if (category.equals(0)) {
            userGameInfoDto = participantWebClientService.getTierAndPlayCount(gameId);
        }

        return userGameInfoDto;
    }

    /**
     * 쿼리 컨트롤러에 반환할 DTO 정제하는 서비스
     *
     * @param participant
     * @return
     */
    private ResponseStatusPlayerDto mapToResponseStatusPlayerDto(Participant participant) {
        ResponseStatusPlayerDto responsePlayerDto = new ResponseStatusPlayerDto();
        responsePlayerDto.setPk(participant.getId());
        responsePlayerDto.setNickname(participant.getNickname());
        responsePlayerDto.setImgSrc(participant.getProfileImageUrl());
        responsePlayerDto.setGameId(participant.getGameId());
        responsePlayerDto.setTier(participant.getGameTier());
        return responsePlayerDto;
    }
}
