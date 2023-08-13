package leaguehub.leaguehubbackend.service.participant;

import leaguehub.leaguehubbackend.dto.channel.ParticipantChannelDto;
import leaguehub.leaguehubbackend.dto.participant.GameRankDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.GameTier;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.email.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.participant.exception.*;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static leaguehub.leaguehubbackend.entity.participant.RequestStatus.*;
import static leaguehub.leaguehubbackend.entity.participant.Role.*;


@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MemberService memberService;
    private final WebClient webClient;
    private final JSONParser jsonParser;
    private final ChannelRepository channelRepository;
    private final ChannelService channelService;
    @Value("${riot-api-key-1}")
    private String riot_api_key;


    public int findParticipantPermission(String channelLink) {

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        if (userDetails == null) {
            return OBSERVER.getNum();
        }

        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        List<Participant> findParticipant = participantRepository.findAllByMemberId(member.getId());

        return findParticipant.stream()
                .filter(participant -> participant.getChannel().getChannelLink() == channelLink)
                .map(participant -> participant.getRole().getNum())
                .findFirst()
                .orElse(OBSERVER.getNum());
    }

    public String findChannelHost(String channelLink) {
        return participantRepository.findParticipantByRoleAndChannel_ChannelLink(HOST, channelLink).getNickname();
    }

    /**
     * 사용자가 지정한 Channel을 참가
     *
     * @param channelLink
     * @return Participant participant
     */
    public Participant participateChannel(String channelLink) {
        Member member = memberService.findCurrentMember();

        Channel channel = channelService.validateChannel(channelLink);

        //중복 검사 추가
        duplicateParticipant(member, channelLink);

        Participant participant = Participant.participateChannel(member, channel);
        participant.newCustomChannelIndex(participantRepository.findMaxIndexByParticipant(member.getId()));
        participantRepository.save(participant);

        return participant;
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
     * 해당 채널의 관전자인 유저들을 조회
     *
     * @param channelLink
     * @return
     */
    public List<ResponseStatusPlayerDto> loadObserverPlayerList(String channelLink) {

        checkRoleHost(channelLink);

        List<Participant> findParticipants = participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, OBSERVER, NOREQUEST);

        return findParticipants.stream()
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

        checkRoleHost(channelLink);

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

        List<Participant> findParticipants = participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, Role.PLAYER, RequestStatus.DONE);

        return findParticipants.stream()
                .map(participant -> {
                    ResponseStatusPlayerDto responsePlayerDto = new ResponseStatusPlayerDto();
                    responsePlayerDto.setPk(participant.getId());
                    responsePlayerDto.setNickname(participant.getNickname());
                    responsePlayerDto.setImgSrc(participant.getProfileImageUrl());
                    responsePlayerDto.setGameId(participant.getGameId());
                    responsePlayerDto.setTier(participant.getGameTier());
                    return responsePlayerDto;
                })
                .collect(Collectors.toList());
    }



    /**
     * 해당 채널의 요청한 참가자를 승인해줌
     *
     * @param channelLink
     * @param participantId
     */
    public void approveParticipantRequest(String channelLink, Long participantId) {
        Channel channel = channelService.validateChannel(channelLink);

        checkRoleHost(channelLink);

        checkRealPlayerCount(channelLink);

        Participant findParticipant = getFindParticipant(channelLink, participantId);

        findParticipant.approveParticipantMatch();

        List<Participant> playerLists = participantRepository
                .findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, PLAYER, DONE);


        channel.updateRealPlayer(playerLists.size());
    }

    /**
     * 해당 채널의 요청한 참가자를 거절함
     *
     * @param channelLink
     * @param participantId
     */
    public void rejectedParticipantRequest(String channelLink, Long participantId) {

        checkRoleHost(channelLink);

        Participant participant = getFindParticipant(channelLink, participantId);

        participant.rejectParticipantRequest();
    }

    /**
     * 사용자를 관리자로 권한을 변경한다.
     *
     * @param channelLink
     * @param participantId
     */
    public void updateHostRole(String channelLink, Long participantId) {

        checkRoleHost(channelLink);
        Participant participant = getFindParticipant(channelLink, participantId);

        participant.updateHostRole();
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
            userGameInfoDto = getTierAndPlayCount(gameId);
        }


        return userGameInfoDto;
    }

    /**
     * 관전자인 사용자가 해당 채널의 경기에 참가
     *
     * @param responseDto
     */
    public void participateMatch(ParticipantDto responseDto) {
        Participant participant = getParticipant(responseDto.getChannelLink());

        checkParticipateMatch(participant);

        ChannelRule channelRule = channelRepository
                .findByChannelLink(responseDto.getChannelLink()).get()
                .getChannelRule();

        checkDuplicateNickname(responseDto.getGameId(), responseDto.getChannelLink());

        String userGameInfo = requestUserGameInfo(responseDto.getGameId());

        GameRankDto tier = searchTier(userGameInfo);

        checkRule(channelRule, userGameInfo, tier);

        participant.updateParticipantStatus(responseDto.getGameId(), getGameTier(tier), responseDto.getNickname());
    }

    /**
     * 해당 채널의 룰을 확인
     *
     * @param channelRule
     * @param userGameInfo
     * @param tier
     */
    public void checkRule(ChannelRule channelRule, String userGameInfo, GameRankDto tier) {

        rankRuleCheck(channelRule, tier);
        playCountRuleCheck(channelRule, userGameInfo);
    }

    private void playCountRuleCheck(ChannelRule channelRule, String userGameInfo) {

        if (channelRule.getPlayCount()) {
            int limitedPlayCount = channelRule.getLimitedPlayCount();
            int userPlayCount = getPlayCount(userGameInfo);
            if (userPlayCount < limitedPlayCount)
                throw new ParticipantInvalidPlayCountException();
        }
    }


    private static void rankRuleCheck(ChannelRule channelRule, GameRankDto tier) {

        if (channelRule.getTier()) {
            int tierMax = getRuleTierAndGrade(channelRule.getTierMax());
            if (tierMax == -1) tierMax = Integer.MAX_VALUE;
            int tierMin = getRuleTierAndGrade(channelRule.getTierMin());
            if (tierMin == -1) tierMin = Integer.MIN_VALUE;
            int userRankScore = GameTier.rankToScore(tier.getGameRank().toString(), tier.getGameGrade());
            if (userRankScore > tierMax || userRankScore < tierMin) throw new ParticipantInvalidRankException();
        }
    }

    public static int getRuleTierAndGrade(String channelRuleRank) {
        if (!channelRuleRank.equals(GlobalConstant.NO_DATA.getData())) {
            String[] ruleRank = channelRuleRank.split(" ");
            String ruleTier = ruleRank[0];
            String ruleGrade = ruleRank[1];
            return GameTier.rankToScore(ruleTier, ruleGrade);
        }

        return -1;
    }

    private String getGameTier(GameRankDto tier) {
        return tier.getGameRank().toString() + " " + tier.getGameGrade();
    }

    /**
     * 해당채널의 관리자가 맞는지 확인
     *
     * @param channelLink
     */
    public void checkRoleHost(String channelLink) {
        Participant participant = getParticipant(channelLink);

        if (participant.getRole().getNum() != HOST.getNum())
            throw new ParticipantNotGameHostException();

    }

    public void checkRealPlayerCount(String channelLink) {
        Channel channel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);

        if (channel.getRealPlayer() >= channel.getMaxPlayer())
            throw new ParticipantRealPlayerIsMaxException();
    }


    /**
     * 이메일이 인증되었는지 확인
     *
     * @param userDetails
     */
    public void checkEmail(UserDetails userDetails) {
        if (!userDetails.getAuthorities().toString().equals("[ROLE_USER]"))
            throw new UnauthorizedEmailException();
    }


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
     * channelLink와 member로 해당 채널에서의 참가자 찾기
     *
     * @param channelLink
     * @param member
     * @return
     */
    private Participant getParticipant(String channelLink, Member member) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink)
                .orElseThrow(ParticipantNotFoundException::new);
        return participant;
    }

    /**
     * 제 3자가 channelLink와 participantId로 해당 채널에서의 참가자 찾기
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

    /**
     * member 찾기
     *
     * @return
     */
    private Member getMember() {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        String personalId = userDetails.getUsername();
        Member member = memberService.validateMember(personalId);
        return member;
    }


}
