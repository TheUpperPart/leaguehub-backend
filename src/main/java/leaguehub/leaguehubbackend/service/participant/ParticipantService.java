package leaguehub.leaguehubbackend.service.participant;

import leaguehub.leaguehubbackend.dto.channel.ParticipantChannelDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantSummonerDetail;
import leaguehub.leaguehubbackend.dto.participant.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.match.MatchPlayerResultStatus;
import leaguehub.leaguehubbackend.entity.match.PlayerStatus;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.GameTier;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.email.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.participant.exception.*;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static leaguehub.leaguehubbackend.entity.member.BaseRole.USER;
import static leaguehub.leaguehubbackend.entity.participant.RequestStatus.*;
import static leaguehub.leaguehubbackend.entity.participant.Role.*;


@Service
@Transactional
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
    private final ChannelRuleRepository channelRuleRepository;
    private final MatchPlayerRepository matchPlayerRepository;


    public int findParticipantPermission(String channelLink) {
        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        if (userDetails == null) return OBSERVER.getNum();
        Member member = memberService.validateMember(userDetails.getUsername());


        Optional<Participant> findParticipant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink);

        return findParticipant.map(participant -> participant.getRole().getNum())
                .orElse(OBSERVER.getNum());
    }

    public String findChannelHost(String channelLink) {
        return participantRepository.findParticipantByRoleAndChannel_ChannelLinkOrderById(HOST, channelLink).get(0).getNickname();
    }

    /**
     * 사용자가 지정한 Channel을 참가
     *
     * @param channelLink
     * @return Participant participant
     */
    public ParticipantChannelDto participateChannel(String channelLink) {

        Member member = memberService.findCurrentMember();

        Channel channel = channelService.getChannel(channelLink);

        //중복 검사 추가
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
     * 해당 채널의 관전자인 유저들을 조회
     *
     * @param channelLink
     * @return
     */
    public List<ResponseStatusPlayerDto> loadObserverPlayerList(String channelLink) {
        Participant findParticipant = getParticipant(channelLink);

        checkRoleHost(findParticipant.getRole());

        List<Participant> findParticipants = participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, OBSERVER, NO_REQUEST);

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
        Participant findParticipant = getParticipant(channelLink);
        checkRoleHost(findParticipant.getRole());

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
     * 해당 채널의 요청한 참가자를 승인해줌
     *
     * @param channelLink
     * @param participantId
     */
    public void approveParticipantRequest(String channelLink, Long participantId) {
        Participant participant = getParticipant(channelLink);
        checkRoleHost(participant.getRole());

        checkRealPlayerCount(participant.getChannel());

        Participant findParticipant = getFindParticipant(channelLink, participantId);

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
        Participant participant = getParticipant(channelLink);
        checkRoleHost(participant.getRole());


        Participant findParticipant = getFindParticipant(channelLink, participantId);

        findParticipant.rejectParticipantRequest();

        updateRealPlayerCount(channelLink, participant.getChannel());
    }

    public void disqualifiedParticipant(String channelLink, Long participantId) {
        Participant findParticipant = checkHostAndGetParticipant(channelLink, participantId);

        disqualificationParticipant(findParticipant);
    }

    public void selfDisqualified(String channelLink, Long participantId){
        //matchPlayerId -> ParticipantId로 변경해야함
        Participant participant = participantRepository.findParticipantByIdAndChannel_ChannelLink(participantId, channelLink)
                .orElseThrow(() -> new ParticipantNotFoundException());

        disqualificationParticipant(participant);
    }

    private void disqualificationParticipant(Participant findParticipant) {
        findParticipant.disqualificationParticipant();
        matchPlayerRepository.findMatchPlayersByParticipantId(findParticipant.getId()).stream()
                .forEach(matchPlayer -> {
                            matchPlayer.updatePlayerCheckInStatus(PlayerStatus.DISQUALIFICATION);
                            matchPlayer.updateMatchPlayerResultStatus(MatchPlayerResultStatus.DISQUALIFICATION);}
                );
    }

    /**
     * 사용자를 관리자로 권한을 변경한다.
     *
     * @param channelLink
     * @param participantId
     */
    public void updateHostRole(String channelLink, Long participantId) {
        Participant findParticipant = checkHostAndGetParticipant(channelLink, participantId);

        findParticipant.updateHostRole();
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
    public void participateMatch(ParticipantDto responseDto, String channelLink) {
        Participant participant = getParticipant(channelLink);

        checkParticipateMatch(participant);

        ChannelRule channelRule = channelRuleRepository
                .findChannelRuleByChannel_ChannelLink(channelLink);

        checkDuplicateNickname(responseDto.getGameId(), channelLink);

        ParticipantSummonerDetail participantSummonerDetail = requestUserGameInfo(responseDto.getGameId());
        String userGameInfo = participantSummonerDetail.getUserGameInfo();
        String puuid = participantSummonerDetail.getPuuid();

        GameTier tier = searchTier(userGameInfo);

        checkRule(channelRule, userGameInfo, tier);

        participant.updateParticipantStatus(responseDto.getGameId(), tier.toString(), responseDto.getNickname(), puuid);
    }

    /**
     * 해당 채널의 룰을 확인
     *
     * @param channelRule
     * @param userGameInfo
     * @param tier
     */
    public void checkRule(ChannelRule channelRule, String userGameInfo, GameTier tier) {

        rankRuleCheck(channelRule, tier);
        playCountRuleCheck(channelRule, userGameInfo);
    }

    public void checkAdminHost(String channelLink) {
        Participant participant = getParticipant(channelLink);
        checkRoleHost(participant.getRole());
    }

    private void playCountRuleCheck(ChannelRule channelRule, String userGameInfo) {

        if (channelRule.getPlayCount()) {
            int limitedPlayCount = channelRule.getLimitedPlayCount();
            int userPlayCount = getPlayCount(userGameInfo);
            if (userPlayCount < limitedPlayCount)
                throw new ParticipantInvalidPlayCountException();
        }
    }


    private static void rankRuleCheck(ChannelRule channelRule, GameTier tier) {

        if (channelRule.getTier()) {
            int tierMax = channelRule.getTierMax();
            int tierMin = channelRule.getTierMin();

            int userRankScore = tier.getScore();
            if (userRankScore > tierMax || userRankScore < tierMin) throw new ParticipantInvalidRankException();
        }
    }


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

    private void updateRealPlayerCount(String channelLink, Channel channel) {
        List<Participant> playerLists = participantRepository
                .findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, PLAYER, DONE);

        channel.updateRealPlayer(playerLists.size());
    }

    private Participant checkHostAndGetParticipant(String channelLink, Long participantId) {
        Participant participant = getParticipant(channelLink);
        checkRoleHost(participant.getRole());

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

    private ResponseStatusPlayerDto mapToResponseStatusPlayerDto(Participant participant) {
        ResponseStatusPlayerDto responsePlayerDto = new ResponseStatusPlayerDto();
        responsePlayerDto.setPk(participant.getId());
        responsePlayerDto.setNickname(participant.getNickname());
        responsePlayerDto.setImgSrc(participant.getProfileImageUrl());
        responsePlayerDto.setGameId(participant.getGameId());
        responsePlayerDto.setTier(participant.getGameTier());
        return responsePlayerDto;
    }

    private void checkParticipateMatch(Participant participant) {

        if (participant.getRole() != OBSERVER
                || participant.getRequestStatus() == DONE) throw new ParticipantInvalidRoleException();

        if (participant.getRequestStatus() == REQUEST) throw new ParticipantAlreadyRequestedException();

        if (participant.getRequestStatus() == REJECT) throw new ParticipantRejectedRequestedException();

    }

    public void checkDuplicateNickname(String gameId, String channelLink) {
        List<Participant> participantList = participantRepository.findAllByChannel_ChannelLink(channelLink);

        boolean checkDuplicate = participantList.stream()
                .anyMatch(participant -> participant.getGameId().equals(gameId));

        if (checkDuplicate) {
            throw new ParticipantDuplicatedGameIdException();
        }
    }

    public void duplicateParticipant(Member member, String channelLink) {
        Optional<Participant> existingParticipant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(member.getId(), channelLink);

        if (existingParticipant.isPresent()) {
            duplicatePlayerIdCheck();
        }
    }

    private void duplicatePlayerIdCheck() {
        throw new ParticipantDuplicatedGameIdException();
    }

    /**
     * 닉네임으로 고유id 추출
     *
     * @param nickname
     * @return id
     */
    public JSONObject getSummonerId(String nickname) {
        String summonerUrl = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";

        JSONObject summonerDetail = webClient.get()
                .uri(summonerUrl + nickname + riot_api_key)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ParticipantGameIdNotFoundException()))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new GlobalServerErrorException()))
                .bodyToMono(JSONObject.class)
                .block();

        return summonerDetail;
    }


    /**
     * 외부 api호출로 유저 상세정보 출력
     *
     * @param nickname
     * @return
     */
    public ParticipantSummonerDetail requestUserGameInfo(String nickname) {

        JSONObject summonerDetail = getSummonerId(nickname);

        String gameId = summonerDetail.get("id").toString();
        String puuid = summonerDetail.get("puuid").toString();

        String tierUrl = "https://kr.api.riotgames.com/tft/league/v1/entries/by-summoner/";


        JSONArray summonerDetails = webClient.get()
                .uri(tierUrl + gameId + riot_api_key)
                .retrieve()
                .bodyToMono(JSONArray.class)
                .block();

        String arraytoString = summonerDetails.toJSONString();

        ParticipantSummonerDetail participantSummonerDetail = new ParticipantSummonerDetail();
        participantSummonerDetail.setPuuid(puuid);
        participantSummonerDetail.setUserGameInfo(arraytoString);

        return participantSummonerDetail;

    }

    /**
     * 고유 id로 티어추출
     *
     * @param userGameInfo
     * @return Tier
     */
    @SneakyThrows
    public GameTier searchTier(String userGameInfo) {

        String jsonToString = userGameInfo.replaceAll("[\\[\\[\\]]", "");

        if (jsonToString.isEmpty()) {
            return GameTier.getUnranked();
        }

        JSONObject summonerDetail = (JSONObject) jsonParser.parse(jsonToString);
        String tier = summonerDetail.get("tier").toString();
        String rank = summonerDetail.get("rank").toString();


        return GameTier.findGameTier(tier, rank);

    }

    /**
     * 플레이 횟수 검색
     *
     * @param userGameInfo
     * @return
     */
    public Integer getPlayCount(String userGameInfo) {

        String jsonToString = userGameInfo.replaceAll("[\\[\\[\\]]", "");

        if (jsonToString.isEmpty())
            return 0;

        return stringToIntegerPlayCount(jsonToString);

    }

    /**
     * 플레이 횟수 문자열을 정수형으로 변환
     *
     * @param userGameInfoJSON
     * @return
     */
    @SneakyThrows
    public Integer stringToIntegerPlayCount(String userGameInfoJSON) {
        JSONObject summonerDetail = (JSONObject) jsonParser.parse(userGameInfoJSON);

        return Integer.parseInt(summonerDetail.get("wins").toString()) + Integer.parseInt(summonerDetail.get("losses").toString());
    }

    /**
     * 티어와 플레이 횟수를 받아 반환
     *
     * @param nickname
     * @return
     */
    public ResponseUserGameInfoDto getTierAndPlayCount(String nickname) {

        ParticipantSummonerDetail participantSummonerDetail = requestUserGameInfo(nickname);
        String userGameInfo = participantSummonerDetail.getUserGameInfo();

        GameTier tier = searchTier(userGameInfo);

        Integer playCount = getPlayCount(userGameInfo);

        ResponseUserGameInfoDto userGameInfoDto = new ResponseUserGameInfoDto();
        userGameInfoDto.setTier(tier.toString());
        userGameInfoDto.setPlayCount(playCount);

        return userGameInfoDto;
    }

    private void checkRoleHost(Role role) {
        if (role != Role.HOST) {
            throw new InvalidParticipantAuthException();
        }
    }

}
