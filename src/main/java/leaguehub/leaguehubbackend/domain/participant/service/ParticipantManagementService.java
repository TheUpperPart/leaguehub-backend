package leaguehub.leaguehubbackend.domain.participant.service;

import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
import leaguehub.leaguehubbackend.domain.match.entity.MatchPlayerResultStatus;
import leaguehub.leaguehubbackend.domain.match.entity.PlayerStatus;
import leaguehub.leaguehubbackend.domain.match.repository.MatchPlayerRepository;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.exception.auth.exception.AuthInvalidTokenException;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.member.service.JwtService;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantIdDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantIdResponseDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantSummonerDetail;
import leaguehub.leaguehubbackend.domain.participant.entity.GameTier;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.*;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static leaguehub.leaguehubbackend.domain.match.entity.PlayerStatus.DISQUALIFICATION;
import static leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus.*;
import static leaguehub.leaguehubbackend.domain.participant.entity.Role.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantManagementService {

    private final MemberService memberService;
    private final ChannelService channelService;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ChannelRuleRepository channelRuleRepository;
    private final ParticipantService participantService;
    private final JwtService jwtService;
    private final ParticipantWebClientService participantWebClientService;

    /**
     * 사용자가 지정한 Channel을 참가
     *
     * @param channelLink
     * @return Participant participant
     */
    public ParticipantChannelDto participateChannel(String channelLink) {

        Member member = memberService.findCurrentMember();

        Channel channel = channelService.getChannel(channelLink);

        duplicateParticipant(member, channelLink);

        Participant participant = Participant.participateChannel(member, channel);
        participant.newCustomChannelIndex(participantRepository.findMaxIndexByParticipant(member.getId()));
        participantRepository.save(participant);

        return new ParticipantChannelDto(
                channel.getId(),
                channel.getChannelLink(),
                channel.getTitle(),
                channel.getGameCategory().getNum(),
                channel.getChannelImageUrl(),
                participant.getIndex()
        );
    }

    /**
     * 해당 채널 나가기
     *
     * @param channelLink
     */
    public void leaveChannel(String channelLink) {
        Member member = memberService.findCurrentMember();

        Participant participant = getParticipant(channelLink, member);

        participantRepository.deleteById(participant.getId());

        List<Participant> participantAfterDelete = participantRepository.findAllByMemberIdAndAndIndexGreaterThan(
                member.getId(), participant.getIndex());

        for (Participant allParticipantByMember : participantAfterDelete) {
            allParticipantByMember.updateCustomChannelIndex(allParticipantByMember.getIndex() - 1);
        }
    }


    /**
     * 대회 참가자 실격 & 기권 서비스
     *
     * @param channelLink
     * @param message
     * @return
     */
    public ParticipantIdResponseDto disqualifiedParticipant(String channelLink, ParticipantIdDto message) {
        if (message.getRole() == HOST.getNum()) {
            return disqualifiedToHost(channelLink, message);
        }

        if (message.getRole() == PLAYER.getNum()) {
            return selfDisqualified(channelLink, message);
        }

        throw new InvalidParticipantAuthException();
    }


    /**
     * 관전자인 사용자가 해당 채널의 경기에 참가
     *
     * @param responseDto
     */
    public void participateMatch(ParticipantDto responseDto, String channelLink) {
        Participant participant = participantService.getParticipant(channelLink);

        checkParticipateMatch(participant);

        ChannelRule channelRule = channelRuleRepository
                .findChannelRuleByChannel_ChannelLink(channelLink);

        checkDuplicateNickname(responseDto.getGameId(), channelLink);

        ParticipantSummonerDetail participantSummonerDetail = participantWebClientService.requestUserGameInfo(responseDto.getGameId());
        String userGameInfo = participantSummonerDetail.getUserGameInfo();
        String puuid = participantSummonerDetail.getPuuid();

        GameTier tier = participantWebClientService.searchTier(userGameInfo);

        checkRule(channelRule, userGameInfo, tier);

        participant.updateParticipantStatus(responseDto.getGameId(), tier.toString(), responseDto.getNickname(), puuid);
    }

    /**
     * 참여 채널의 순서를 커스텀
     * @param participantChannelDtoList
     * @return
     */
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
     * 참가자 중복검사
     *
     * @param member
     * @param channelLink
     */
    private void duplicateParticipant(Member member, String channelLink) {
        Optional<Participant> existingParticipant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink);

        if (existingParticipant.isPresent()) {
            throw new ParticipantDuplicatedGameIdException();
        }
    }


    /**
     * 관리자가 직접 참가자 실격시키는 메서드
     *
     * @param channelLink
     * @param message
     * @return
     */
    private ParticipantIdResponseDto disqualifiedToHost(String channelLink, ParticipantIdDto message) {
        Participant myParticipant = findParticipantAccessToken(channelLink, message.getAccessToken());
        participantService.checkRole(myParticipant.getRole(), HOST);

        Participant findParticipant = participantService.getFindParticipant(channelLink, message.getParticipantId());

        disqualificationParticipant(findParticipant);

        return new ParticipantIdResponseDto(message.getMatchPlayerId(), DISQUALIFICATION.getStatus());
    }


    /**
     * 참가자가 직접 기권하는 메서드
     *
     * @param channelLink
     * @param message
     * @return
     */
    private ParticipantIdResponseDto selfDisqualified(String channelLink, ParticipantIdDto message) {
        Participant myParticipant = findParticipantAccessToken(channelLink, message.getAccessToken());
        participantService.checkRole(myParticipant.getRole(), PLAYER);

        disqualificationParticipant(myParticipant);

        return new ParticipantIdResponseDto(message.getMatchPlayerId(), DISQUALIFICATION.getStatus());
    }


    /**
     * 상태를 실격으로 변경시키는 메서드
     *
     * @param findParticipant
     */
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

    /**
     * AccessToken을 찾는 메서드
     *
     * @param channelLink
     * @param accessToken
     * @return
     */
    private Participant findParticipantAccessToken(String channelLink, String accessToken) {
        String personalId = jwtService.extractPersonalId(accessToken)
                .orElseThrow(() -> new AuthInvalidTokenException());
        Member member = memberRepository.findMemberByPersonalId(personalId).get();
        Participant myParticipant = getParticipant(channelLink, member);
        return myParticipant;
    }

    /**
     * 채널에 이미 참가되어 있는지 확인하는 메서드
     *
     * @param participant
     */
    private void checkParticipateMatch(Participant participant) {

        if (participant.getRole() != OBSERVER
                || participant.getRequestStatus() == DONE) throw new ParticipantInvalidRoleException();

        if (participant.getRequestStatus() == REQUEST) throw new ParticipantAlreadyRequestedException();

        if (participant.getRequestStatus() == REJECT) throw new ParticipantRejectedRequestedException();

    }

    private void checkDuplicateNickname(String gameId, String channelLink) {
        List<Participant> participantList = participantRepository.findAllByChannel_ChannelLink(channelLink);

        boolean checkDuplicate = participantList.stream()
                .anyMatch(participant -> participant.getGameId().equals(gameId));

        if (checkDuplicate) {
            throw new ParticipantDuplicatedGameIdException();
        }
    }

    /**
     * 해당 채널의 룰을 확인
     *
     * @param channelRule
     * @param userGameInfo
     * @param tier
     */
    private void checkRule(ChannelRule channelRule, String userGameInfo, GameTier tier) {

        rankRuleCheck(channelRule, tier);
        playCountRuleCheck(channelRule, userGameInfo);
    }

    private static void rankRuleCheck(ChannelRule channelRule, GameTier tier) {

        if (channelRule.getTier()) {
            int tierMax = channelRule.getTierMax();
            int tierMin = channelRule.getTierMin();

            int userRankScore = tier.getScore();
            if (userRankScore > tierMax || userRankScore < tierMin) throw new ParticipantInvalidRankException();
        }
    }

    private void playCountRuleCheck(ChannelRule channelRule, String userGameInfo) {

        if (channelRule.getPlayCount()) {
            int limitedPlayCount = channelRule.getLimitedPlayCount();
            int userPlayCount = participantWebClientService.getPlayCount(userGameInfo);
            if (userPlayCount < limitedPlayCount)
                throw new ParticipantInvalidPlayCountException();
        }
    }

    /**
     * channelLink와 member로 해당 채널에서의 참가자 찾기 -
     * @param channelLink
     * @param member
     * @return
     */
    private Participant getParticipant(String channelLink, Member member) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink)
                .orElseThrow(ParticipantNotFoundException::new);
        return participant;
    }


}
