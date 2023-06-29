package leaguehub.leaguehubbackend.service.participant;

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

    @Value("${riot-api-key-1}")
    private String riot_api_key;

    private final WebClient webClient;

    private final JSONParser jsonParser;

    public String selectGameCategory(String gameId, Integer category){
        String tier = "";
        if(category.equals(0))
            tier = getTier(gameId);

        return tier;
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
     * 고유 id로 티어추출
     * @param nickname
     * @return Tier
     */
    @SneakyThrows
    public String getTier(String nickname){

        String gameId = getSummonerId(nickname);

        String tierUrl = "https://kr.api.riotgames.com/tft/league/v1/entries/by-summoner/";


        JSONArray summonerDetails = webClient.get()
                .uri(tierUrl + gameId + riot_api_key)
                .retrieve()
                .bodyToMono(JSONArray.class)
                .block();

        String arraytoString = summonerDetails.toJSONString();

        String jsonToString = arraytoString.replaceAll("[\\[\\[\\]]", "");

        if(jsonToString.isEmpty())
            return "unranked";

        JSONObject summonerDetail = (JSONObject) jsonParser.parse(jsonToString);

        return summonerDetail.get("tier").toString() + " " + summonerDetail.get("rank").toString();
    }


}
