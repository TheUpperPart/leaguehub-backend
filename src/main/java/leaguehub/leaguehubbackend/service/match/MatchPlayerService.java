package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.*;
import leaguehub.leaguehubbackend.entity.match.*;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchAlreadyUpdateException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchPlayerNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchSetRepository;
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
import java.util.stream.Collectors;

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
    private final MatchPlayerRepository matchPlayerRepository;
    private final MatchSetRepository matchSetRepository;
    private final MatchService matchService;

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
     * 라이엇 API로 경기 결과 호출
     *
     * @param gameId
     * @return matchRankResultDto
     */
    @SneakyThrows
    public RiotAPIDto getMatchDetailFromRiot(String gameId) {
        String puuid = getSummonerPuuid(gameId);
        String riotMatchUuid = getMatch(puuid);

        JSONObject matchDetailJSON = responseMatchDetail(riotMatchUuid);


        JSONObject info = (JSONObject) jsonParser.parse(matchDetailJSON.get("info").toString());
        JSONArray participantList = (JSONArray) jsonParser.parse(info.get("participants").toString());

        List<MatchRankResultDto> matchRankResultDtoList = setPlacement(participantList);


        return new RiotAPIDto(riotMatchUuid, matchRankResultDtoList);
    }

    public void updateMatchPlayerScore(Long matchId, Integer setCount) {
        List<MatchPlayer> findMatchPlayerList = matchPlayerRepository.findMatchPlayersWithoutDisqualification(matchId);

        RiotAPIDto matchDetailFromRiot = getMatchDetailFromRiot(findMatchPlayerList.get(0).getParticipant().getGameId());

        MatchSet matchSet = getMatchSet(matchId, setCount);

        matchSet.updateRiotMatchUuid(matchDetailFromRiot.getMatchUuid());

        List<MatchRankResultDto> matchRankResultDtoList = matchDetailFromRiot.getMatchRankResultDtoList();

        validMatchResult(findMatchPlayerList, matchRankResultDtoList);

        matchRankResultDtoList
                .forEach(matchRankResultDto ->
                        findMatchPlayerList.stream()
                                .filter(matchPlayer -> matchPlayer.getId().equals(matchRankResultDto.getGameId()))
                                .forEach(matchPlayer -> matchPlayer.updateMatchPlayerScore(matchRankResultDto.getPlacement()))
                );

        matchSet.updateScore(true);

        Match match = findMatchPlayerList.get(0).getMatch();
        checkMatchEnd(matchSet, match);
    }

    /**
     * MatchResult에 실격한 멤버를 제외한 모든 멤버가 있는지 체크
     * @param findMatchPlayerList
     * @param matchRankResultDtoList
     */
    private void validMatchResult(List<MatchPlayer> findMatchPlayerList, List<MatchRankResultDto> matchRankResultDtoList) {
        long count = matchRankResultDtoList.stream()
                .flatMap(dto -> findMatchPlayerList.stream()
                        .filter(player -> dto.getGameId().equals(player.getParticipant().getGameId())))
                .count();

        if (count != findMatchPlayerList.size()) {
            throw new MatchResultIdNotFoundException();
        }
    }

    private void checkMatchEnd(MatchSet matchSet, Match match) {
        if(match.getRoundMaxCount() == matchSet.getSetCount()) {
            match.updateMatchStatus(MatchStatus.END);
        }
    }

    private List<MatchPlayer> getMatchPlayers(Long matchId) {
        List<MatchPlayer> findMatchPlayerList = matchPlayerRepository.findMatchPlayersAndMatchAndParticipantByMatchId(matchId);

        if (findMatchPlayerList.size() == 0) {
            throw new MatchNotFoundException();
        }
        return findMatchPlayerList;
    }


    private MatchSet getMatchSet(Long matchId, Integer setCount) {
        MatchSet matchSet = matchSetRepository.findMatchSetByMatchIdAndAndSetCount(matchId, setCount)
                .orElseThrow(() -> new MatchNotFoundException());

        if (matchSet.getUpdateScore()) {
            throw new MatchAlreadyUpdateException();
        }

        return matchSet;
    }

    private MatchPlayer findMatchPlayer(Long matchPlayerId, Long matchId) {
        return matchPlayerRepository.findByParticipantIdAndMatchId(matchPlayerId, matchId)
                .orElseThrow(MatchPlayerNotFoundException::new);
    }

    @Transactional
    public void markPlayerAsReady(MatchSetReadyMessage message, Long matchId) {

        Long matchPlayerId = message.getMatchPlayerId();

        MatchPlayer matchPlayer = findMatchPlayer(matchPlayerId, matchId);

        matchPlayer.changeStatusToReady();
        matchPlayerRepository.save(matchPlayer);
    }

    public List<MatchSetStatusMessage> getAllPlayerStatusForMatch(Long matchId) {
        List<MatchPlayer> matchPlayers = matchPlayerRepository.findAllByMatch_Id(matchId);

        if (matchPlayers.isEmpty()) {
            throw new MatchNotFoundException();
        }

        return matchPlayers.stream()
                .map(mp -> new MatchSetStatusMessage (mp.getId(), mp.getPlayerStatus()))
                .collect(Collectors.toList());
    }
}
