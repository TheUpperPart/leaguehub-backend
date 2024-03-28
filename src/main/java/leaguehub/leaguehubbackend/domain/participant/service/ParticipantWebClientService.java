package leaguehub.leaguehubbackend.domain.participant.service;

import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantSummonerDetail;
import leaguehub.leaguehubbackend.domain.participant.dto.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.domain.participant.entity.GameTier;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantGameIdNotFoundException;
import leaguehub.leaguehubbackend.global.exception.global.exception.GlobalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantWebClientService {


    private final WebClient webClient;
    private final JSONParser jsonParser;

    @Value("${riot-api-key-1}")
    private String riot_api_key;


    /**
     * 닉네임 + 태크로 고유 puuid 추출
     * 받은 nickname으로 split 나누기
     */
    public String getSummonerPUuid(String nickname) {
        String pUuidURL = "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/";

        String gameId = nickname.split("#")[0];
        String gameTag = nickname.split("#")[1];


        JSONObject userAccount = webClient.get()
                .uri(pUuidURL + gameId + "/" + gameTag + riot_api_key)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ParticipantGameIdNotFoundException()))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new GlobalServerErrorException()))
                .bodyToMono(JSONObject.class)
                .block();

        return userAccount.get("puuid").toString();
    }

    /**
     * 고유 puuid로 유저의 정보 추출
     *
     * @param nickname
     * @return id
     */
    public JSONObject getSummonerId(String nickname) {
        String puuid = getSummonerPUuid(nickname);

        String summonerUrl = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-puuid/";

        return webClient.get()
                .uri(summonerUrl + puuid + riot_api_key)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ParticipantGameIdNotFoundException()))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new GlobalServerErrorException()))
                .bodyToMono(JSONObject.class)
                .block();
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

}
