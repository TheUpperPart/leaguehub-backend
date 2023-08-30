package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchPlayerService {

    private final WebClient webClient;
    private final JSONParser jsonParser;
    @Value("${riot-api-key-1}")
    private String riot_api_key_1;
    @Value("${riot-api-key-2}")
    private String riot_api_key_2;

    /**
     * 소환사의 라이엇 puuid를 얻는 메서드
     *
     * @param name 게임 닉네임
     * @return puuid
     */
    public String getSummonerPuuid(String name) {
        String summonerUrl = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/";


        JSONObject summonerDetail = webClient.get()
                .uri(summonerUrl + name + riot_api_key_1)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ParticipantGameIdNotFoundException()))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new GlobalServerErrorException()))
                .bodyToMono(JSONObject.class)
                .block();

        String puuid = Objects.requireNonNull(summonerDetail).get("puuid").toString();

        return puuid;
    }

    /**
     * 게임 Id로 얻은 puuid로 라이엇 서버에 고유 매치 Id 검색
     *
     * @param puuid
     * @return
     */
    public String getMatch(String puuid) {
        long endTime = System.currentTimeMillis() / 1000;
        long statTime = 0;

        String matchUrl = "https://asia.api.riotgames.com/tft/match/v1/matches/by-puuid/";
        String Option = "/ids?start=0&endTime=" + endTime + "&startTime=" + statTime + "&count=1";


        JSONArray matchArray = webClient.get()
                .uri(matchUrl + puuid + Option + riot_api_key_2)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new MatchResultIdNotFoundException()))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new GlobalServerErrorException()))
                .bodyToMono(JSONArray.class)
                .block();


        return matchArray.get(0).toString();

    }


    @SneakyThrows
    public JSONObject responseMatchDetail(String matchId) {
        String matchDetailUrl = "https://asia.api.riotgames.com/tft/match/v1/matches/";

        return (JSONObject) jsonParser.parse
                (webClient
                        .get()
                        .uri(matchDetailUrl + matchId + riot_api_key_1)
                        .retrieve()
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new GlobalServerErrorException()))
                        .bodyToMono(JSONObject.class)
                        .block().toJSONString());
    }


    @SneakyThrows
    public List<MatchRankResultDto> setPlacement(JSONArray participantList) {
        List<MatchRankResultDto> dtoList = new ArrayList<>();


        for (int i = 0; i < 8; i++) {
            JSONObject participants = (JSONObject) jsonParser.parse(participantList.get(i).toString());
            Integer placement = Integer.parseInt(participants.get("placement").toString());
            String parti1puuid = participants.get("puuid").toString();


            String summonerName = webClient.get()
                    .uri("https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-puuid/" + parti1puuid + riot_api_key_1)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new GlobalServerErrorException()))
                    .bodyToMono(JSONObject.class)
                    .block().get("name").toString();

            MatchRankResultDto matchRankResultDto = new MatchRankResultDto();
            matchRankResultDto.setGameId(summonerName);
            matchRankResultDto.setPlacement(placement);

            dtoList.add(matchRankResultDto);

        }
        return dtoList;
    }


    /**
     * 최근 매치 id로 경기 세부사항 추출
     *
     * @param matchResponseDto
     * @return matchRankResultDto
     */
    @SneakyThrows
    public List<MatchRankResultDto> setMatchRank(MatchResponseDto matchResponseDto) {
        String puuid = getSummonerPuuid(matchResponseDto.getGameId());
        String matchId = getMatch(puuid);

        JSONObject matchDetailJSON = responseMatchDetail(matchId);


        JSONObject info = (JSONObject) jsonParser.parse(matchDetailJSON.get("info").toString());
        JSONArray participantList = (JSONArray) jsonParser.parse(info.get("participants").toString());

        List<MatchRankResultDto> matchRankResultDtoList = setPlacement(participantList);


        return matchRankResultDtoList;
    }
}
