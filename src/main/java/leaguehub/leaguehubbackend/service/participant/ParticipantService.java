package leaguehub.leaguehubbackend.service.participant;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final String RGAPI = "?api_key=RGAPI-3e4095a9-91ad-413d-b716-b60b17a8aedd";


    public String getSummonerId(String nickname){
        String summonerUrl = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";

        WebClient webClient = WebClient.create();
        JSONObject summonerDetail = webClient.get()
                .uri(summonerUrl + nickname + RGAPI)
                .retrieve()
                .onStatus(status -> status.is4xxClientError()
                                || status.is5xxServerError()
                        , response ->
                                response.bodyToMono(String.class)
                                        .map(RuntimeException::new))
                .bodyToMono(JSONObject.class)
                .block();

        String id = summonerDetail.get("id").toString();

        return id;
    }


    public String getTier(String nickname){

        String gameId = getSummonerId(nickname);

        String tierUrl = "https://kr.api.riotgames.com/tft/league/v1/entries/by-summoner/";

        JSONParser jsonParser = new JSONParser();
        WebClient webClient = WebClient.create();
        JSONArray summonerDetails = webClient.get()
                .uri(tierUrl + gameId + RGAPI)
                .retrieve()
                .bodyToMono(JSONArray.class)
                .block();

        String arraytoString = summonerDetails.toJSONString();

        try {
            String jsonToString = arraytoString.replaceAll("[\\[\\[\\]]", "");
            JSONObject summonerDetail = (JSONObject) jsonParser.parse(jsonToString);

            return summonerDetail.get("tier").toString() + " " + summonerDetail.get("rank").toString();

        } catch (ParseException e) {
            return "unranked";
        }

    }
}
