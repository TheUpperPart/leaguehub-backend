package leaguehub.leaguehubbackend.service.participant;

import leaguehub.leaguehubbackend.dto.participant.GameRankDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import leaguehub.leaguehubbackend.dto.participant.RequestPlayerDto;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.GameTier;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.participant.exception.*;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static leaguehub.leaguehubbackend.entity.participant.Role.HOST;
import static leaguehub.leaguehubbackend.entity.participant.Role.OBSERVER;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class ParticipantService {

    @Value("${riot-api-key-1}")
    private String riot_api_key;

    private final ParticipantRepository participantRepository;
    private final ChannelRuleRepository channelRuleRepository;
    private final MemberService memberService;

    private final WebClient webClient;
    private final JSONParser jsonParser;


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
     * 해당 채널의 PLAYER 역할인 유저들을 반환
     * @param channelLink
     * @return RequestPlayerDtoList
     */
    public List<RequestPlayerDto> loadPlayers(Long channelLink){

        List<Participant> findParticipants = participantRepository.findAllByChannelIdOrderByNicknameAsc(channelLink);

        List<RequestPlayerDto> dtoList = new ArrayList<>();


        for(Participant participant : findParticipants){
            if(participant.getRole().getNum() == Role.PLAYER.getNum()){
                RequestPlayerDto playerDto = new RequestPlayerDto();
                playerDto.setName(participant.getNickname());
                playerDto.setImgSrc(participant.getProfileImageUrl());
                playerDto.setGameId(participant.getGameId());

                dtoList.add(playerDto);
            }
        }

        return dtoList;
    }

    /**
     * 참가하기 버튼을 통하여 참가 자격이 있는지 확인
     * @param channelId 해당 채널 아이디
     */
    public Participant checkParticipateMatch(Long channelId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getDetails();

        if (userDetails == null) {
            throw new ParticipantInvalidLoginException();
        }

        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        Participant participant = participantRepository.findParticipantByMemberIdAndChannelId(member.getId(), channelId);

        if(participant.getRole().getNum() != 3)
            throw new ParticipantInvalidRoleException();

        return participant;
    }

    /**
     * 게임 카테고리에 따라 요청 분할
     * @param gameId
     * @param category
     * @return
     */
    public ResponseUserDetailDto selectGameCategory(String gameId, Integer category){
        ResponseUserDetailDto userDetailDto = new ResponseUserDetailDto();

        if(category.equals(0)){
            userDetailDto = getTierAndPlayCount(gameId);
        }


        return userDetailDto;
    }

    /**
     * 닉네임으로 고유id 추출
     * @param nickname
     * @return id
     */
    public String getSummonerId(String nickname){
        String summonerUrl = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";

        JSONObject summonerDetail = webClient.get()
                .uri(summonerUrl + nickname + riot_api_key)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ParticipantGameIdNotFoundException()))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new GlobalServerErrorException()))
                .bodyToMono(JSONObject.class)
                .block();

        return summonerDetail.get("id").toString();
    }

    /**
     * 외부 api호출로 유저 상세정보 출력
     * @param nickname
     * @return
     */
    public String requestUserDetail(String nickname){

        String gameId = getSummonerId(nickname);

        String tierUrl = "https://kr.api.riotgames.com/tft/league/v1/entries/by-summoner/";


        JSONArray summonerDetails = webClient.get()
                .uri(tierUrl + gameId + riot_api_key)
                .retrieve()
                .bodyToMono(JSONArray.class)
                .block();

        String arraytoString = summonerDetails.toJSONString();

        return arraytoString;

    }

    /**
     * 고유 id로 티어추출
     *
     * @param userDetail
     * @return Tier
     */
    @SneakyThrows
    public GameRankDto searchTier(String userDetail){

        String jsonToString = userDetail.replaceAll("[\\[\\[\\]]", "");

        if(jsonToString.isEmpty()){
            return GameTier.getUnranked();
        }

        JSONObject summonerDetail = (JSONObject) jsonParser.parse(jsonToString);
        String tier = summonerDetail.get("tier").toString();
        String rank = summonerDetail.get("rank").toString();
        String leaguePoints = summonerDetail.get("leaguePoints").toString();

        if(tier.equalsIgnoreCase("master") ||
                tier.equalsIgnoreCase("grandmaster")
                || tier.equalsIgnoreCase("challenger")){
            return GameTier.getRanked(tier, leaguePoints);
        }

        return GameTier.findGameTier(tier, rank);

    }

    /**
     * 플레이 횟수 검색
     * @param userDetail
     * @return
     */
    public Integer getPlayCount(String userDetail){

        String jsonToString = userDetail.replaceAll("[\\[\\[\\]]", "");

        if(jsonToString.isEmpty())
            return 0;

        return stringToIntegerPlayCount(jsonToString);

    }

    /**
     * 플레이 횟수 문자열을 정수형으로 변환
     * @param userDetailJSON
     * @return
     */
    @SneakyThrows
    public Integer stringToIntegerPlayCount(String userDetailJSON){
        JSONObject summonerDetail = (JSONObject) jsonParser.parse(userDetailJSON);

        return Integer.parseInt(summonerDetail.get("wins").toString()) + Integer.parseInt(summonerDetail.get("losses").toString());
    }

    /**
     * 티어와 플레이 횟수를 받아 반환
     * @param nickname
     * @return
     */
    public ResponseUserDetailDto getTierAndPlayCount(String nickname){

        String userDetail = requestUserDetail(nickname);

        GameRankDto tier = searchTier(userDetail);

        Integer playCount = getPlayCount(userDetail);

        ResponseUserDetailDto userDetailDto = new ResponseUserDetailDto();
        userDetailDto.setTier(tier.getGameRank().toString());
        userDetailDto.setGrade(tier.getGameGrade());
        userDetailDto.setPlayCount(playCount);

        return userDetailDto;
    }

    /**
     * 해당 채널의 룰을 확인
     * @param channelRule
     * @param userDetail
     * @param tier
     */
    public void checkRule(ChannelRule channelRule, String userDetail, GameRankDto tier){
        if(channelRule.getTier()){
            int limitedRankScore = GameTier.rankToScore(channelRule.getLimitedTier(), channelRule.getLimitedGrade());
            int userRankScore = GameTier.rankToScore(tier.getGameRank().toString(), tier.getGameGrade());
            if(userRankScore > limitedRankScore)
                throw new ParticipantInvalidRankException();
        }
        if(channelRule.getPlayCount()){
            int limitedPlayCount = channelRule.getLimitedPlayCount();
            int userPlayCount = getPlayCount(userDetail);
            if(userPlayCount < limitedPlayCount)
                throw new ParticipantInvalidPlayCountException();
        }
    }

    /**
     * 관전자인 사용자가 해당 채널의 경기에 참가
     * @param responseDto
     */
    public void participateMatch(ParticipantResponseDto responseDto){

        Participant participant = checkParticipateMatch(responseDto.getChannelId());

        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannelId(responseDto.getChannelId());

        String userDetail = requestUserDetail(responseDto.getNickname());

        GameRankDto tier = searchTier(userDetail);

        checkRule(channelRule, userDetail, tier);

        participant.updateParticipantStatus(responseDto.getNickname(), tier.getGameRank().toString());
    }




}
