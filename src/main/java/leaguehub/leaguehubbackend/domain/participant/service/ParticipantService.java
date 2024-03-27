package leaguehub.leaguehubbackend.domain.participant.service;

import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
import leaguehub.leaguehubbackend.domain.email.exception.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.domain.match.entity.MatchPlayerResultStatus;
import leaguehub.leaguehubbackend.domain.match.entity.PlayerStatus;
import leaguehub.leaguehubbackend.domain.match.repository.MatchPlayerRepository;
import leaguehub.leaguehubbackend.domain.member.entity.BaseRole;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.dto.*;
import leaguehub.leaguehubbackend.domain.participant.entity.GameTier;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.entity.Role;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.*;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static leaguehub.leaguehubbackend.domain.match.entity.PlayerStatus.DISQUALIFICATION;
import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.GUEST;
import static leaguehub.leaguehubbackend.domain.member.entity.BaseRole.USER;
import static leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus.*;
import static leaguehub.leaguehubbackend.domain.participant.entity.Role.*;


@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MemberService memberService;
    private final WebClient webClient;
    private final JSONParser jsonParser;
    private final ChannelService channelService;
    @Value("${riot-api-key-1}")
    private String riot_api_key;
    private final ChannelRuleRepository channelRuleRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private final ParticipantWebClientService participantWebClientService;


    //참여자의 권한 확인하는 서비스 - > 쿼리(조회해서 그냥 넘겨주기 떄문에)
    public int findParticipantPermission(String channelLink) {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        if (userDetails == null) return OBSERVER.getNum();
        Member member = memberService.validateMember(userDetails.getUsername());


        Optional<Participant> findParticipant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink);

        return findParticipant.map(participant -> participant.getRole().getNum())
                .orElse(OBSERVER.getNum());
    }

    //해당 채널의 첫 번째 관리자를 찾는 서비스 - 쿼리
    public String findChannelHost(String channelLink) {
        return participantRepository.findParticipantByRoleAndChannel_ChannelLinkOrderById(HOST, channelLink).get(0).getNickname();
    }


    //채널 유저 조회 서비스  -> 쿼리

    /**
     * 해당 채널의 관전자인 유저들을 조회
     *
     * @param channelLink
     * @return
     */
    public List<ResponseStatusPlayerDto> loadObserverPlayerList(String channelLink) {
        Participant findParticipant = getParticipant(channelLink);

        checkRole(findParticipant.getRole(), HOST);

        List<Participant> findParticipants = participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, OBSERVER, NO_REQUEST);

        return findParticipants.stream()
                .filter(participant -> participant.getMember().getBaseRole() == USER)
                .map(participant -> mapToResponseStatusPlayerDto(participant))
                .collect(Collectors.toList());
    }

    //채널 유저 조회 서비스 --> 쿼리

    /**
     * 요청을 보낸 사람들을 조회
     *
     * @param channelLink
     * @return
     */
    public List<ResponseStatusPlayerDto> loadRequestStatusPlayerList(String channelLink) {
        Participant findParticipant = getParticipant(channelLink);
        checkRole(findParticipant.getRole(), HOST);

        List<Participant> findParticipants =
                participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc
                        (channelLink, OBSERVER, REQUEST);

        return findParticipants.stream()
                .map(participant -> mapToResponseStatusPlayerDto(participant))
                .collect(Collectors.toList());
    }

    //채널 유저 조회 서비스 -->쿼리

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


    //관리자가 대회 참가자 실격 서비스 -> 커맨드
    private ParticipantIdResponseDto disqualifiedToHost(String channelLink, ParticipantIdDto message) {
        Participant myParticipant = findParticipantAccessToken(channelLink, message.getAccessToken());
        checkRole(myParticipant.getRole(), HOST);

        Participant findParticipant = getFindParticipant(channelLink, message.getParticipantId());

        disqualificationParticipant(findParticipant);

        return new ParticipantIdResponseDto(message.getMatchPlayerId(), DISQUALIFICATION.getStatus());
    }

    //대회 참가자가 기권 서비스 -> 커맨드
    private ParticipantIdResponseDto selfDisqualified(String channelLink, ParticipantIdDto message) {
        Participant myParticipant = findParticipantAccessToken(channelLink, message.getAccessToken());
        checkRole(myParticipant.getRole(), PLAYER);

        disqualificationParticipant(myParticipant);

        return new ParticipantIdResponseDto(message.getMatchPlayerId(), DISQUALIFICATION.getStatus());
    }

    //기권 시키는 서비스 -> 커맨드
    private void disqualificationParticipant(Participant findParticipant) {
        findParticipant.disqualificationParticipant();
        matchPlayerRepository.findMatchPlayersByParticipantId(findParticipant.getId())
                .forEach(matchPlayer -> {
                            matchPlayer.updatePlayerCheckInStatus(PlayerStatus.DISQUALIFICATION);
                            matchPlayer.updateMatchPlayerResultStatus(MatchPlayerResultStatus.DISQUALIFICATION);
                            matchPlayer.updateMatchPlayerScoreDisqualified();
                        }
                );
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

    //게임 카테고리에 따른 전적 검색 서비스 -> 쿼리

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


    //해당 사용자가 호스트인지 확인 -> 커맨드
    public void checkAdminHost(String channelLink) {
        Participant participant = getParticipant(channelLink);
        checkRole(participant.getRole(), HOST);
    }


    //해당 사용자의 AccessToken 확인 서비스 - 커맨드(내부 동작)
    private Participant findParticipantAccessToken(String channelLink, String accessToken) {
        String personalId = jwtService.extractPersonalId(accessToken)
                .orElseThrow(() -> new AuthInvalidTokenException());
        Member member = memberRepository.findMemberByPersonalId(personalId).get();
        Participant myParticipant = getParticipant(channelLink, member);
        return myParticipant;
    }


    //해당 채널의 경기 참여자 횟수 체크 - 커맨드
    private void checkRealPlayerCount(Channel channel) {
        if (channel.getRealPlayer() >= channel.getMaxPlayer())
            throw new ParticipantRealPlayerIsMaxException();
    }


    //사용자의 이메일 인증인지 확인 - 내부 동작 여기 남는다

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

    //

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

    //제 3자가 채널에서의 참가자 찾기

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


    //자기 자신의 id를 가져오는 서비스 -> 내부동작 여기 남는다

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

    //쿼리 컨트롤러에 반환할 dto를 정제하는 서비스 -> 쿼리 내부 메소드로 들어간다
    private ResponseStatusPlayerDto mapToResponseStatusPlayerDto(Participant participant) {
        ResponseStatusPlayerDto responsePlayerDto = new ResponseStatusPlayerDto();
        responsePlayerDto.setPk(participant.getId());
        responsePlayerDto.setNickname(participant.getNickname());
        responsePlayerDto.setImgSrc(participant.getProfileImageUrl());
        responsePlayerDto.setGameId(participant.getGameId());
        responsePlayerDto.setTier(participant.getGameTier());
        return responsePlayerDto;
    }

    //해당 채널의 Rule이 맞는지 확인 -> 여기에 남는다
    public void checkRole(Role myRole, Role checkRole) {
        if (myRole != checkRole) {
            throw new InvalidParticipantAuthException();
        }
    }

}
