package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.MatchRankResultDto;
import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.dto.match.MatchResultUpdateDto;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.match.MatchRank;
import leaguehub.leaguehubbackend.entity.match.MatchResult;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.global.exception.GlobalServerErrorException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchPlayerNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchResultIdNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRankRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.match.MatchResultRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
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
public class MatchRankService {

    private final WebClient webClient;
    private final JSONParser jsonParser;
    private final MatchResultService matchResultService;
    private final MatchRankRepository matchRankRepository;
    private final MatchResultRepository matchResultRepository;
    private final MemberService memberService;
    @Value("${riot-api-key-1}")
    private String riot_api_key_1;
    @Value("${riot-api-key-2}")
    private String riot_api_key_2;
    private final MatchRepository matchRepository;


    /**
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
     * puuid로 매치 id 검색
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
    public List<MatchRankResultDto> setPlacement(JSONArray participantList, MatchResult matchResult) {
        List<MatchRankResultDto> dtoList = new ArrayList<>();


        for (int i = 0; i < 8; i++) {
            JSONObject participants = (JSONObject) jsonParser.parse(participantList.get(i).toString());
            String placement = participants.get("placement").toString();
            String parti1puuid = participants.get("puuid").toString();


            String summonerName = webClient.get()
                    .uri("https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-puuid/" + parti1puuid + riot_api_key_1)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new GlobalServerErrorException()))
                    .bodyToMono(JSONObject.class)
                    .block().get("name").toString();

            MatchRankResultDto dto = new MatchRankResultDto();
            dto.setName(summonerName);
            dto.setPlacement(placement);

            dtoList.add(dto);

            MatchRank matchRank = MatchRank.createMatchRank(summonerName, placement, matchResult);
            matchRankRepository.save(matchRank);


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
        String puuid = getSummonerPuuid(matchResponseDto.getNickName());
        String matchId = getMatch(puuid);

        JSONObject matchDetailJSON = responseMatchDetail(matchId);
        //uuid를 이용하여 찾는다 dto에 uuid와 matchId만 필요함
        MatchResult matchResult = matchResultService.saveMatchResult(
                matchResponseDto.getMatchLink(),
                matchId);


        JSONObject info = (JSONObject) jsonParser.parse(matchDetailJSON.get("info").toString());
        JSONArray participantList = (JSONArray) jsonParser.parse(info.get("participants").toString());

        List<MatchRankResultDto> matchRankResultDtoList = setPlacement(participantList, matchResult);


        return matchRankResultDtoList;
    }

    /**
     * 해당 경기 순위 반환
     *
     * @param matchCode
     * @return MatchRankResultDto
     */
    public List<MatchRankResultDto> getMatchDetail(String matchCode) {

        List<MatchRankResultDto> dtoList = new ArrayList<>();

        Long id = matchResultRepository.findByMatchCode(matchCode).getId();

        List<MatchRank> list = matchRankRepository.findByMatchResult_Id(id);

        for (MatchRank matchRank : list) {
            MatchRankResultDto dto = new MatchRankResultDto();
            dto.setName(matchRank.getParticipant());
            dto.setPlacement(matchRank.getPlacement());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public void updateMatchPlayerPlacement(MatchResultUpdateDto matchResultUpdateDto) {
        Member member = memberService.findCurrentMember();

        Match match = getMatch(matchResultUpdateDto.getMatchId());

        MatchPlayer findMatchPlayer = getMatchPlayer(matchResultUpdateDto, match);

        checkAuthMatchPlayerUpdate(member, findMatchPlayer);

        MatchRank findMatchRank = getMatchRank(match, findMatchPlayer);

        findMatchRank.updateMatchRank(matchResultUpdateDto.getPlacement());
    }

    private void checkAuthMatchPlayerUpdate(Member member, MatchPlayer findMatchPlayer) {
        if(findMatchPlayer.getParticipant().getMember().getId() != member.getId()) {
            if(findMatchPlayer.getParticipant().getRole() != Role.HOST) {
                throw new InvalidParticipantAuthException();
            }
        }
    }

    private MatchRank getMatchRank(Match match, MatchPlayer findMatchPlayer) {
        MatchRank findMatchRank = match.getMatchRankList().stream().filter(matchRank ->
                        (matchRank.getMatch().getId() == match.getId())
                                && (matchRank.getMatchPlayer().getId() == findMatchPlayer.getId()))
                .findFirst()
                .orElseThrow(() -> new MatchResultIdNotFoundException());
        return findMatchRank;
    }

    private MatchPlayer getMatchPlayer(MatchResultUpdateDto matchResultUpdateDto, Match match) {
        MatchPlayer findMatchPlayer = match.getMatchPlayerList().stream()
                .filter(matchPlayer -> matchPlayer.getId() == matchResultUpdateDto.getMatchPlayerId())
                .findFirst()
                .orElseThrow(() -> new MatchPlayerNotFoundException());
        return findMatchPlayer;
    }

    private Match getMatch(Long matchId) {
        return matchRepository.findMatchAndMatchPlayerAndMatchRank(matchId)
                .orElseThrow(() -> new MatchNotFoundException());
    }
}
