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
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


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

    @Value("${riot-api-key-1}")
    private String riot_api_key;

    private final WebClient webClient;

    private final JSONParser jsonParser;

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
     * @param userDetail
     * @return Tier
     */
    @SneakyThrows
    public String getTier(String userDetail){

        String jsonToString = userDetail.replaceAll("[\\[\\[\\]]", "");

        if(jsonToString.isEmpty())
            return "unranked";

        JSONObject summonerDetail = (JSONObject) jsonParser.parse(jsonToString);

        return summonerDetail.get("tier").toString() + " " + summonerDetail.get("rank").toString();
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

        return stringToInteger(jsonToString);

    }

    /**
     * 플레이 횟수 문자열을 정수형으로 변환
     * @param userDetailJSON
     * @return
     */
    @SneakyThrows
    public Integer stringToInteger(String userDetailJSON){
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

        String tier = getTier(userDetail);

        Integer playCount = getPlayCount(userDetail);

        ResponseUserDetailDto userDetailDto = new ResponseUserDetailDto();
        userDetailDto.setTier(tier);
        userDetailDto.setPlayCount(playCount);

        return userDetailDto;
    }


}
