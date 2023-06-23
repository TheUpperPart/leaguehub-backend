package leaguehub.leaguehubbackend.service.participant;

import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    @Value("${riot-api-key}")
    private String riot_api_key;


    public String getSummonerId(String nickname){
        String summonerUrl = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";

        WebClient webClient = WebClient.create();

        JSONObject summonerDetail = webClient.get()
                .uri(summonerUrl + nickname + riot_api_key)
                .retrieve()
                .onStatus(status -> status.is4xxClientError()
                                || status.is5xxServerError()
                        , response -> Mono.error(new ParticipantGameIdNotFoundException()))
                .bodyToMono(JSONObject.class)
                .block();

        String id = summonerDetail.get("id").toString();

        return id;
    }


    @SneakyThrows
    public String getTier(String nickname){

        String gameId = getSummonerId(nickname);

        String tierUrl = "https://kr.api.riotgames.com/tft/league/v1/entries/by-summoner/";

        JSONParser jsonParser = new JSONParser();
        WebClient webClient = WebClient.create();
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
