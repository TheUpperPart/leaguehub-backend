package leaguehub.leaguehubbackend.domain.match.service;

import leaguehub.leaguehubbackend.domain.match.dto.*;
import leaguehub.leaguehubbackend.domain.match.entity.*;
import leaguehub.leaguehubbackend.domain.match.exception.exception.MatchAlreadyUpdateException;
import leaguehub.leaguehubbackend.domain.match.exception.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.domain.match.exception.exception.MatchPlayerNotFoundException;
import leaguehub.leaguehubbackend.domain.match.exception.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.domain.match.repository.MatchPlayerRepository;
import leaguehub.leaguehubbackend.domain.match.repository.MatchRankRepository;
import leaguehub.leaguehubbackend.domain.match.repository.MatchRepository;
import leaguehub.leaguehubbackend.domain.match.repository.MatchSetRepository;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantIdResponseDto;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.ParticipantGameIdNotFoundException;
import leaguehub.leaguehubbackend.global.exception.global.exception.GlobalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static leaguehub.leaguehubbackend.domain.match.entity.MatchPlayerResultStatus.ADVANCE;
import static leaguehub.leaguehubbackend.domain.match.entity.MatchPlayerResultStatus.DROPOUT;
import static leaguehub.leaguehubbackend.domain.match.entity.PlayerStatus.READY;
import static leaguehub.leaguehubbackend.domain.match.entity.PlayerStatus.WAITING;

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
    private final MatchRepository matchRepository;
    private final MatchRankRepository matchRankRepository;

    /**
     * 소환사의 라이엇 puuid를 얻는 메서드
     *
     * @param name 게임 닉네임
     * @return puuid
     */
    public String getSummonerPuuid(String name) {
        String gameId = name.split("#")[0];
        String gameTag = name.split("#")[1];

        String summonerPuuidUrl = "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/";

        JSONObject userAccount = webClient.get()
                .uri(summonerPuuidUrl + gameId + "/" + gameTag + riot_api_key_1)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ParticipantGameIdNotFoundException()))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new GlobalServerErrorException()))
                .bodyToMono(JSONObject.class)
                .block();


        String puuid =  userAccount.get("puuid").toString();

        return puuid;

    }

    /**
     * 게임 Id로 얻은 puuid로 라이엇 서버에 고유 매치 Id 검색
     *
     * @param puuid
     * @return
     */
    public String getMatch(String puuid, long endTime) {
//        long endTime = System.currentTimeMillis() / 1000;
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
    public List<MatchRankResultDto> setPlacement(JSONArray participantList, List<MatchPlayer> findMatchPlayerList) {
        List<MatchRankResultDto> dtoList = new ArrayList<>();


        for (int jsonIndex = 0; jsonIndex < 8; jsonIndex++) {

            JSONObject participants = (JSONObject) jsonParser.parse(participantList.get(jsonIndex).toString());

            Integer placement = Integer.parseInt(participants.get("placement").toString());

            String parti1puuid = participants.get("puuid").toString();

            MatchRankResultDto matchRankResultDto = new MatchRankResultDto();
            findMatchPlayerList.stream()
                    .map(MatchPlayer::getParticipant)
                    .filter(participant -> participant.getPuuid().equalsIgnoreCase(parti1puuid))
                    .forEach(participant -> {
                        matchRankResultDto.setGameId(participant.getGameId());
                        matchRankResultDto.setPlacement(placement);
                        dtoList.add(matchRankResultDto);
                    });
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
    public RiotAPIDto getMatchDetailFromRiot(String gameId, List<MatchPlayer> findMatchPlayerList, Long endTime) {
        String puuid = getSummonerPuuid(gameId);
        String riotMatchUuid = getMatch(puuid, endTime);

        JSONObject matchDetailJSON = responseMatchDetail(riotMatchUuid);


        JSONObject info = (JSONObject) jsonParser.parse(matchDetailJSON.get("info").toString());
        JSONArray participantList = (JSONArray) jsonParser.parse(info.get("participants").toString());

        List<MatchRankResultDto> matchRankResultDtoList = setPlacement(participantList, findMatchPlayerList);


        return new RiotAPIDto(riotMatchUuid, matchRankResultDtoList);
    }

    public MatchInfoDto updateMatchPlayerScore(Long matchId, Integer setCount, Long endTime) {
        List<MatchPlayer> findMatchPlayerList = matchPlayerRepository.findMatchPlayersWithoutDisqualification(matchId);

        if(findMatchPlayerList.size() == 0) throw new MatchNotFoundException();

        RiotAPIDto matchDetailFromRiot =
                getMatchDetailFromRiot(findMatchPlayerList.get(0).getParticipant().getGameId(),
                        findMatchPlayerList, endTime);

        MatchSet matchSet = getMatchSet(matchId, setCount);

        if (matchSet.getRiotMatchUuid() == null) matchSet.updateRiotMatchUuid(matchDetailFromRiot.getMatchUuid());

        List<MatchRankResultDto> matchRankResultDtoList = matchDetailFromRiot.getMatchRankResultDtoList();
        validMatchResult(findMatchPlayerList, matchRankResultDtoList);
        Collections.sort(matchRankResultDtoList, Comparator.comparing(MatchRankResultDto::getPlacement));

        replaceMatchResult(findMatchPlayerList.stream()
                .map(matchPlayer -> matchPlayer.getParticipant().getGameId()).collect(Collectors.toList()), matchRankResultDtoList);

        matchRankResultDtoList
                .forEach(matchRankResultDto ->
                        findMatchPlayerList.stream()
                                .filter(matchPlayer -> matchPlayer.getParticipant().getGameId().equals(matchRankResultDto.getGameId()))
                                .forEach(matchPlayer -> matchPlayer.updateMatchPlayerScore(matchRankResultDto.getPlacement()))
                );


        matchSet.updateScore(true);

        List<MatchRank> matchRanks = matchRankResultDtoList.stream()
                .map(dto -> MatchRank.createMatchRank(matchSet, dto.getGameId(), dto.getPlacement()))
                .collect(Collectors.toList());
        matchSet.addMatchRankList(matchRanks);
        matchRankRepository.saveAll(matchRanks);

        findMatchPlayerList.stream()
                .forEach(matchPlayer -> matchPlayer.updatePlayerCheckInStatus(WAITING));

        Match match = findMatchPlayerList.get(0).getMatch();
        checkMatchEnd(matchSet, match);

        List<MatchPlayer> allByMatchId = matchPlayerRepository.findAllByMatch_Id(matchId);
        MatchInfoDto matchInfoDto = matchService.convertMatchInfoDto(match, allByMatchId);

        return matchInfoDto;
    }

    private void replaceMatchResult(List<String> findMatchPlayerGameIdList, List<MatchRankResultDto> matchRankResultDtoList) {
        matchRankResultDtoList.removeIf(matchRankResultDto ->
                !findMatchPlayerGameIdList.contains(matchRankResultDto.getGameId()));

        matchRankResultDtoList.stream().sorted(Comparator.comparing(MatchRankResultDto::getPlacement));

        IntStream.range(0, matchRankResultDtoList.size())
                .forEach(i -> matchRankResultDtoList.get(i).setPlacement(i + 1));
    }

    public List<GameResultDto> getGameResult(Long matchId) {
        List<MatchSet> matchSets = matchSetRepository.findMatchSetsByMatch_Id(matchId);
        if (matchSets.isEmpty()) throw new MatchResultIdNotFoundException();
        List<GameResultDto> gameResultDtoList = matchSets.stream().map(matchSet -> GameResultDto.builder()
                .matchSetCount(matchSet.getSetCount()).matchRankResultDtos(
                        matchSet.getMatchRankList().stream().map(matchRank -> new MatchRankResultDto(matchRank.getGameId(), matchRank.getPlacement()))
                                .collect(Collectors.toList())
                ).build()).collect(Collectors.toList());

        gameResultDtoList.sort(Comparator.comparing(GameResultDto::getMatchSetCount));

        return gameResultDtoList;
    }

    /**
     * MatchResult에 실격한 멤버를 제외한 모든 멤버가 있는지 체크
     *
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

    /**
     * 매치가 끝난지 체크하는 로직
     * 매치가 끝났다면 매치 상태를 업데이트하고 updateEndMatchResult로 진출자, 탈락자를 결정한다.
     *
     * @param matchSet
     * @param match
     */
    private void checkMatchEnd(MatchSet matchSet, Match match) {
        if (match.getMatchSetCount().equals(matchSet.getSetCount())) {
            match.updateMatchStatus(MatchStatus.END);
            updateEndMatchResult(match);
        } else {
            match.updateCurrentMatchSet(matchSet.getSetCount() + 1);
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
        return matchPlayerRepository.findMatchPlayerByIdAndMatch_Id(matchPlayerId, matchId)
                .orElseThrow(MatchPlayerNotFoundException::new);
    }

    @Transactional
    public ParticipantIdResponseDto markPlayerAsReady(MatchSetReadyMessage message, String matchIdStr) {

        Long matchId = Long.valueOf(matchIdStr);
        Long matchPlayerId = message.getMatchPlayerId();

        MatchPlayer matchPlayer = findMatchPlayer(matchPlayerId, matchId);

        if(matchPlayer.getMatchPlayerResultStatus() != MatchPlayerResultStatus.PROGRESS) {
            throw new MatchAlreadyUpdateException();
        }

        if(matchPlayer.getMatchPlayerResultStatus() == MatchPlayerResultStatus.DISQUALIFICATION){
            throw new InvalidParticipantAuthException();
        }

        matchPlayer.updatePlayerCheckInStatus(READY);

        return new ParticipantIdResponseDto(message.getMatchPlayerId(), READY.getStatus());
    }

    public List<MatchSetStatusMessage> getAllPlayerStatusForMatch(Long matchId) {
        List<MatchPlayer> matchPlayers = matchPlayerRepository.findAllByMatch_Id(matchId);

        if (matchPlayers.isEmpty()) {
            throw new MatchNotFoundException();
        }

        return matchPlayers.stream()
                .map(mp -> new MatchSetStatusMessage(mp.getId(), mp.getPlayerStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 매치 종료 후 진출자, 탈락자를 결정한다.
     * 실격을 제외한 매치 플레이어들을 점수대로 정렬해 불러와서
     * 4번째 위치한 선수를 기준으로 동점자, 진출자, 탈락자를 결정한다.
     *
     * @param match
     */
    public void updateEndMatchResult(Match match) {
        List<MatchPlayer> matchPlayersWithoutDisqualification = matchPlayerRepository.findMatchPlayersWithoutDisqualification(match.getId());
        Integer advanceScore = matchPlayersWithoutDisqualification.get(3).getPlayerScore();

        long winCount = advanceMatchPlayer(matchPlayersWithoutDisqualification, advanceScore);

        List<MatchPlayer> tieMatchPlayerList = getTiePlayerList(matchPlayersWithoutDisqualification, advanceScore);

        dropoutMatchPlayerWithScore(matchPlayersWithoutDisqualification, advanceScore);

        if (winCount == 3 && tieMatchPlayerList.size() == 1) {
            MatchPlayer matchPlayer = tieMatchPlayerList.get(0);
            matchPlayer.updateMatchPlayerResultStatus(ADVANCE);
        } else if (winCount < 4 && tieMatchPlayerList.size() > 1) {
            tieBreaker(tieMatchPlayerList, match.getId(), 4 - Long.valueOf(winCount).intValue());
        }
    }

    private void dropoutMatchPlayerWithScore(List<MatchPlayer> matchPlayersWithoutDisqualification, Integer advanceScore) {
        matchPlayersWithoutDisqualification.stream()
                .filter(mp -> mp.getPlayerScore() < advanceScore)
                .forEach(mp -> {
                    dropoutMatchPlayerAndParticipantStatus(mp);
                });
    }

    private void dropoutMatchPlayerAndParticipantStatus(MatchPlayer mp) {
        mp.updateMatchPlayerResultStatus(DROPOUT);
        mp.getParticipant().dropoutParticipantStatus();
    }

    @NotNull
    private List<MatchPlayer> getTiePlayerList(List<MatchPlayer> matchPlayersWithoutDisqualification, Integer advanceScore) {
        List<MatchPlayer> tieMatchPlayerList = matchPlayersWithoutDisqualification.stream()
                .filter(mp -> mp.getPlayerScore().equals(advanceScore))
                .collect(Collectors.toList());
        return tieMatchPlayerList;
    }

    private long advanceMatchPlayer(List<MatchPlayer> matchPlayersWithoutDisqualification, Integer advanceScore) {
        long winCount = matchPlayersWithoutDisqualification.stream()
                .filter(mp -> mp.getPlayerScore() > advanceScore)
                .peek(mp -> mp.updateMatchPlayerResultStatus(ADVANCE))
                .count();
        return winCount;
    }


    /**
     * 동점자 처리 로직
     * i. 1등을 많이 한 플레이어
     * ii. 가장 최근 게임 등수에서 가장 높은 순위를 가진 플레이어
     *
     * @param tieMatchPlayerList
     * @param matchId
     */
    public void tieBreaker(List<MatchPlayer> tieMatchPlayerList, Long matchId, Integer advanceCount) {
        List<GameResultDto> matchSetResult = getGameResult(matchId);

        //게임 Id만 뽑는 로직
        List<String> tiePlayerGameIdList = tieMatchPlayerList.stream()
                .map(matchPlayer -> matchPlayer.getParticipant().getGameId())
                .collect(Collectors.toList());

        int tiePlayerCount = tieMatchPlayerList.size();

        //가장 많이 1등 한 사람들을 뽑는 로직
        List<String> firstPlayer = mostFirstPlayer(matchSetResult, tiePlayerGameIdList);

        //진출 숫자가 1등 플레이어보다 많으면 1등 플레이어 모두 진출
        if (advanceCount - firstPlayer.size() >= 0) {
            //1등 플레이어 제외 모두 drop으로 바뀜
            updateMatchPlayerStatus(tieMatchPlayerList, firstPlayer);
            tieMatchPlayerList.removeIf(matchPlayer -> firstPlayer.contains(matchPlayer.getParticipant().getGameId()));
            tiePlayerGameIdList.removeAll(firstPlayer);
            advanceCount -= firstPlayer.size();
            tiePlayerCount -= firstPlayer.size();
        } else {
            //그게 아니면 1등 플레이어들만 남기고 전부 탈락
            updateMatchPlayerStatus(tieMatchPlayerList, firstPlayer);
            tieMatchPlayerList.removeIf(matchPlayer -> !firstPlayer.contains(matchPlayer.getParticipant().getGameId()));
        }

        //advanceCount가 0보다 크면 들어감, 만약 advanceCount가 0이면, 위에서 전부 Drop 했기 때문에 그냥 넘어감
        if (advanceCount > 0) {
            //1등 플레이어 로직으로 걸러진 동점자들이 남은 advanceCount보다 크면 최근 경기 등수 대로, 만약 작거나 같으면 전부 진출
            if (tiePlayerCount > advanceCount) {
                List<String> tiePlayerGameIdOfAdvanceList = lastGamePlacement(tiePlayerGameIdList, matchSetResult, advanceCount);
                updateMatchPlayerStatus(tieMatchPlayerList, tiePlayerGameIdOfAdvanceList);
            } else {
                updateMatchPlayerStatus(tieMatchPlayerList, tiePlayerGameIdList);
            }
        }
    }

    private void updateMatchPlayerStatus(List<MatchPlayer> tieMatchPlayerList, List<String> tiePlayerGameIdList) {
        for (MatchPlayer matchPlayer : tieMatchPlayerList) {
            if (tiePlayerGameIdList.contains(matchPlayer.getParticipant().getGameId())) {
                matchPlayer.updateMatchPlayerResultStatus(ADVANCE);
            } else {
                dropoutMatchPlayerAndParticipantStatus(matchPlayer);
            }
        }
    }


    /**
     * 가장 1등을 많이 한 플레이어(들)을 가져오는 로직
     * 없으면 동점자들을 들어온 그대로 다시 반환한다.
     * 있는데 여러명이라면 여러명을 다시 반환해 tiePlayerGameIdList로 만들어버린다.
     *
     * @param matchSetResult
     * @param tiePlayerGameIdList
     * @return
     */
    private List<String> mostFirstPlayer(List<GameResultDto> matchSetResult, List<String> tiePlayerGameIdList) {
        Map<String, Integer> countFirstPlayerMap = new ConcurrentHashMap<>();

        int maxCount = 0;

        for (GameResultDto gameResult : matchSetResult) {
            for (MatchRankResultDto matchRankResultDto : gameResult.getMatchRankResultDtos()) {
                if (matchRankResultDto.getPlacement() == 1) {
                    String gameId = matchRankResultDto.getGameId();
                    int count = countFirstPlayerMap.getOrDefault(gameId, 0) + 1;
                    countFirstPlayerMap.put(gameId, count);
                    maxCount = Math.max(maxCount, count);
                }
            }
        }

        List<String> mostFirstPlayerInTieList = new ArrayList<>();
        for (String gameId : countFirstPlayerMap.keySet()) {
            if (maxCount == countFirstPlayerMap.get(gameId) && tiePlayerGameIdList.contains(gameId)) {
                mostFirstPlayerInTieList.add(gameId);
            }
        }

        if (!mostFirstPlayerInTieList.isEmpty()) {
            return mostFirstPlayerInTieList;
        }

        return tiePlayerGameIdList;
    }

    /**
     * 가장 최근 게임에서 가장 높은 등수를 가진 참가자를 뽑는 로직
     * 가장 최근 게임은 가장 최근에 수정된 gameResult 로 판단함
     *
     * @param tiePlayerGameIdList
     * @param matchSetResult
     * @return
     */
    private List<String> lastGamePlacement(List<String> tiePlayerGameIdList, List<GameResultDto> matchSetResult, Integer advanceCount) {

        List<MatchRankResultDto> matchRankResultDtos = matchSetResult.stream().filter(gameResultDto -> gameResultDto.getMatchSetCount() == 3)
                .findFirst().orElseThrow(() -> new MatchNotFoundException()).getMatchRankResultDtos();


        matchRankResultDtos.sort(Comparator.comparing(MatchRankResultDto::getPlacement));

        List<String> tiePlayerGameIdOfAdvanceList = new ArrayList<>();


        for (MatchRankResultDto matchRankResultDto : matchRankResultDtos) {
            String resultGameId = matchRankResultDto.getGameId();
            if (tiePlayerGameIdList.contains(resultGameId) && advanceCount > 0) {
                tiePlayerGameIdOfAdvanceList.add(resultGameId);
                advanceCount--;
            }
        }

        return tiePlayerGameIdOfAdvanceList;
    }

}
