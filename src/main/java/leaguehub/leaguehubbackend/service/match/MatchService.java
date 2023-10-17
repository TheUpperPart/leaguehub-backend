package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.*;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.match.MatchSet;
import leaguehub.leaguehubbackend.entity.match.MatchStatus;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelRequestException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelStatusAlreadyException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotEnoughPlayerException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantRejectedRequestedException;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.match.MatchSetRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static leaguehub.leaguehubbackend.entity.channel.ChannelStatus.PROCEEDING;
import static leaguehub.leaguehubbackend.entity.constant.GlobalConstant.NO_DATA;

import static leaguehub.leaguehubbackend.entity.match.MatchStatus.END;
import static leaguehub.leaguehubbackend.entity.participant.ParticipantStatus.*;

import static leaguehub.leaguehubbackend.entity.participant.Role.PLAYER;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchService {

    private static final int MIN_PLAYERS_FOR_SUB_MATCH = 8;
    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ChannelRepository channelRepository;
    private final ParticipantRepository participantRepository;
    private final MatchSetRepository matchSetRepository;
    private final MemberService memberService;
    private static final int INITIAL_RANK = 1;


    /**
     * 채널을 만들 때 빈 값인 매치를 만듦
     *
     * @param channel
     * @param maxPlayers
     */
    public void createSubMatches(Channel channel, int maxPlayers) {
        int currentPlayers = maxPlayers;
        int matchRoundIndex = 1;

        while (currentPlayers >= MIN_PLAYERS_FOR_SUB_MATCH) {
            currentPlayers = createSubMatchesForRound(channel, currentPlayers, matchRoundIndex);
            matchRoundIndex++;
        }
    }

    /**
     * 해당 채널의 매치 라운드를 보여줌(64, 32, 16, 8)
     *
     * @param channelLink
     * @return 2 4 8 16 32 64
     */
    public MatchRoundListDto getRoundList(String channelLink) {
        Channel findChannel = getChannel(channelLink);

        int maxPlayers = findChannel.getMaxPlayer();
        List<Integer> roundList = calculateRoundList(maxPlayers);

        MatchRoundListDto roundListDto = new MatchRoundListDto();
        roundListDto.setLiveRound(0);
        roundListDto.setRoundList(roundList);

        findLiveRound(channelLink, roundList, roundListDto);

        return roundListDto;
    }

    /**
     * 경기 배정
     *
     * @param channelLink
     * @param matchRound
     */
    public void matchAssignment(String channelLink, Integer matchRound) {
        Participant participant = checkHost(channelLink);

        if(!participant.getChannel().getChannelStatus().equals(PROCEEDING)){
            throw new ChannelRequestException();
        }

        List<Match> matchList = findMatchList(channelLink, matchRound);


        if (matchRound != 1)
            checkUpdateScore(matchList);

        checkPreviousMatchEnd(channelLink, matchRound);

        List<Participant> playerList = getParticipantList(channelLink, matchRound);

        assignSubMatches(matchList, playerList);
    }


    public MatchRoundInfoDto loadMatchPlayerList(String channelLink, Integer matchRound) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);

        List<Match> matchList = findMatchList(channelLink, matchRound);

        List<MatchInfoDto> matchInfoDtoList = matchList.stream()
                .map(this::createMatchInfoDto)
                .collect(Collectors.toList());

        MatchRoundInfoDto matchRoundInfoDto = new MatchRoundInfoDto();

        findMyRoundName(participant, matchList, matchRoundInfoDto);

        matchRoundInfoDto.setMatchInfoDtoList(matchInfoDtoList);
        return matchRoundInfoDto;
    }

    public MyMatchDto getMyMatchRound(String channelLink) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);

        MyMatchDto myMatchDto = new MyMatchDto();

        myMatchDto.setMyMatchRound(0);
        myMatchDto.setMyMatchId(0L);

        findMyMatch(channelLink, participant, myMatchDto);

        return myMatchDto;
    }



    public void setMatchSetCount(String channelLink, List<Integer> roundCount){
        Participant participant = checkHost(channelLink);

        checkChannelProceeding(participant);

        List<Match> findMatchList = matchRepository.findAllByChannel_ChannelLink(channelLink);

        if(findMatchList.isEmpty())
            throw new MatchNotFoundException();

        updateMatchSetCount(roundCount, findMatchList);
    }

    public List<Integer> getMatchSetCount(String channelLink){

        List<Match> matchList = matchRepository.findAllByChannel_ChannelLinkOrderByMatchRoundDesc(channelLink);
        List<Integer> matchSetCountList = getMatchSetCountList(matchList);

        return matchSetCountList;
    }

    public void processMatchSet(String channelLink){
        List<Match> matchList = matchRepository.findAllByChannel_ChannelLink(channelLink);

        createMatchSet(matchList);
    }

    public MatchCallAdminDto callAdmin(String channelLink, Long matchId, Long participantId){
        Participant participant = participantRepository.findParticipantByIdAndChannel_ChannelLink(participantId, channelLink)
                .orElseThrow(() -> new ParticipantNotFoundException());

        if(!participant.getParticipantStatus().equals(PROGRESS)) { throw new ParticipantRejectedRequestedException(); }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException());

        match.updateCallAlarm();

        MatchCallAdminDto matchCallAdminDto = new MatchCallAdminDto();
        matchCallAdminDto.setCallName(participant.getNickname());
        matchCallAdminDto.setMatchRound(match.getMatchRound());
        matchCallAdminDto.setMatchName(match.getMatchName());

        return matchCallAdminDto;
    }

    public void turnOffAlarm(String channelLink, Long matchId){
        Participant participant = checkHost(channelLink);

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException());

        match.updateOffAlarm();
    }

    private void findMyMatch(String channelLink, Participant participant, MyMatchDto myMatchDto) {
        if(participant.getRole().equals(PLAYER)
                && participant.getChannel().getChannelStatus().equals(PROCEEDING)){
            matchRepository.findAllByChannel_ChannelLink(channelLink).stream()
                    .filter(match -> !match.getMatchStatus().equals(END))
                    .flatMap(match -> getMatchPlayerList(match).stream())
                    .filter(matchPlayer -> isSameParticipant(matchPlayer, participant))
                    .findFirst()
                    .ifPresent(matchPlayer -> setMyMatchInfo(myMatchDto, matchPlayer.getMatch()));
        }
    }

    private List< MatchPlayer> getMatchPlayerList(Match match) {
        return  matchPlayerRepository.findAllByMatch_IdOrderByPlayerScoreDesc(match.getId());
    }

    private boolean isSameParticipant(MatchPlayer matchPlayer, Participant participant) {
        return  matchPlayer.getParticipant().getId().equals(participant.getId());
    }

    private void setMyMatchInfo(MyMatchDto mymatchDTO, Match match){
        mymatchDTO.setMyMatchId(match.getId());
        mymatchDTO.setMyMatchRound(match.getMatchRound());
    }

    private Channel getChannel(String channelLink) {
        Channel findChannel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(() -> new ChannelNotFoundException());
        return findChannel;
    }

    private List<Integer> calculateRoundList(int maxPlayers) {
        List<Integer> defaultroundList = Arrays.asList(0, 8, 16, 32, 64, 128, 256);

        int roundIndex = defaultroundList.indexOf(maxPlayers);

        if (roundIndex == -1) {
            throw new ChannelNotFoundException();// 에러 처리 시 빈 리스트 반환
        }

        return IntStream.rangeClosed(1, roundIndex)
                .boxed()
                .collect(Collectors.toList());
    }

    private void findLiveRound(String channelLink, List<Integer> roundList, MatchRoundListDto roundListDto) {
        roundList.forEach(round -> {
                    List<Match> matchList = findMatchList(channelLink, round);
                    matchList.stream()
                            .filter(match -> match.getMatchStatus().equals(MatchStatus.PROGRESS))
                            .findFirst()
                            .ifPresent(match -> roundListDto.setLiveRound(match.getMatchRound()));
                }
        );
    }

    private int createSubMatchesForRound(Channel channel, int maxPlayers, int matchRoundIndex) {
        int currentPlayers = maxPlayers;
        int tableCount = currentPlayers / MIN_PLAYERS_FOR_SUB_MATCH;

        for (int tableIndex = 1; tableIndex <= tableCount; tableIndex++) {
            String groupName = "Group " + (char) (64 + tableIndex);
            Match match = Match.createMatch(matchRoundIndex, channel, groupName);
            matchRepository.save(match);
        }

        return currentPlayers / 2;
    }

    private Participant getParticipant(Long memberId, String channelLink) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_ChannelLink(memberId, channelLink)
                .orElseThrow(() -> new InvalidParticipantAuthException());
        return participant;
    }

    private void checkRoleHost(Role role) {
        if (role != Role.HOST) {
            throw new InvalidParticipantAuthException();
        }
    }

    private List<Match> findMatchList(String channelLink, Integer matchRound) {
        List<Match> matchList = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channelLink, matchRound);
        return matchList;
    }

    private List<Participant> getParticipantList(String channelLink, Integer matchRound) {
        List<Participant> playerList = participantRepository
                .findAllByChannel_ChannelLinkAndRoleAndParticipantStatus(channelLink, PLAYER, PROGRESS);

        if (playerList.size() < matchRound * 0.75) throw new MatchNotEnoughPlayerException();
        return playerList;
    }

    private void assignSubMatches(List<Match> matchList, List<Participant> playerList) {
        Collections.shuffle(playerList);

        int totalPlayers = playerList.size();
        int matchCount = matchList.size();
        int playersPerMatch = totalPlayers / matchCount;
        int remainingPlayers = totalPlayers % matchCount;
        int playerIndex = 0;

        for (Match match : matchList) {
            int currentPlayerCount = playersPerMatch + (remainingPlayers > 0 ? 1 : 0);

            for (int i = 0; i < currentPlayerCount; i++) {
                Participant player = playerList.get(playerIndex);
                MatchPlayer matchPlayer = MatchPlayer.createMatchPlayer(player, match);
                matchPlayerRepository.save(matchPlayer);

                playerIndex++;
                remainingPlayers--;
            }

            match.updateMatchStatus(MatchStatus.PROGRESS);
        }
    }

    private MatchInfoDto createMatchInfoDto(Match match) {
        MatchInfoDto matchInfoDto = new MatchInfoDto();
        matchInfoDto.setMatchName(match.getMatchName());
        matchInfoDto.setMatchId(match.getId());
        matchInfoDto.setMatchStatus(match.getMatchStatus());
        matchInfoDto.setMatchRound(match.getMatchRound());
        matchInfoDto.setMatchCurrentSet(match.getMatchCurrentSet());
        matchInfoDto.setMatchSetCount(match.getMatchSetCount());
        matchInfoDto.setAlarm(match.isAlarm());

        List<MatchPlayer> playerList = matchPlayerRepository.findAllByMatch_IdOrderByPlayerScoreDesc(match.getId());
        List<MatchPlayerInfo> matchPlayerInfoList = createMatchPlayerInfoList(playerList);
        matchInfoDto.setMatchPlayerInfoList(matchPlayerInfoList);

        return matchInfoDto;
    }

    private List<MatchPlayerInfo> createMatchPlayerInfoList(List<MatchPlayer> playerList) {
        List<MatchPlayerInfo> matchPlayerInfoList = playerList.stream()
                .map(matchPlayer -> {
                    MatchPlayerInfo matchPlayerInfo = new MatchPlayerInfo();
                    matchPlayerInfo.setMatchPlayerId(matchPlayer.getId());
                    matchPlayerInfo.setParticipantId(matchPlayer.getParticipant().getId());
                    matchPlayerInfo.setGameId(matchPlayer.getParticipant().getGameId());
                    matchPlayerInfo.setGameTier(matchPlayer.getParticipant().getGameTier());
                    matchPlayerInfo.setPlayerStatus(matchPlayer.getPlayerStatus());
                    matchPlayerInfo.setScore(matchPlayer.getPlayerScore());
                    matchPlayerInfo.setProfileSrc(matchPlayer.getParticipant().getProfileImageUrl());
                    return matchPlayerInfo;
                })
                .collect(Collectors.toList());

        return matchPlayerInfoList;

    }

    public MatchInfoDto getMatchInfo(Long matchId) {
        List<MatchPlayer> matchPlayers = matchPlayerRepository.findMatchPlayersAndMatchAndParticipantByMatchId(matchId);

        return convertMatchInfoDto(matchPlayers.get(0).getMatch(), matchPlayers);
    }

    private void findMyRoundName(Participant participant, List<Match> matchList, MatchRoundInfoDto matchRoundInfoDto) {
        matchRoundInfoDto.setMyGameId(NO_DATA.getData());

        if (!participant.getGameId().equalsIgnoreCase(NO_DATA.getData())) {
            matchList.forEach(match -> {
                List<MatchPlayer> playerList = matchPlayerRepository.findAllByMatch_IdOrderByPlayerScoreDesc(match.getId());
                playerList.stream()
                        .filter(player -> participant.getGameId().equalsIgnoreCase(player.getParticipant().getGameId()))
                        .findFirst()
                        .ifPresent(player -> matchRoundInfoDto.setMyGameId(participant.getGameId()));
            });
        }
    }

    public MatchInfoDto convertMatchInfoDto(Match match, List<MatchPlayer> matchPlayers) {
        return MatchInfoDto.builder().matchId(match.getId())
                .matchName(match.getMatchName())
                .matchStatus(match.getMatchStatus())
                .matchRound(match.getMatchRound())
                .matchSetCount(match.getMatchSetCount())
                .matchCurrentSet(match.getMatchCurrentSet())
                .matchPlayerInfoList(convertMatchPlayerInfoList(matchPlayers))
                .matchAlarm(match.isAlarm())
                .build();
    }


    public List<MatchPlayerInfo> convertMatchPlayerInfoList(List<MatchPlayer> matchPlayers) {
        List<MatchPlayerInfo> matchPlayerInfoList = matchPlayers.stream()
                .map(matchPlayer -> new MatchPlayerInfo(
                        matchPlayer.getId(),
                        matchPlayer.getParticipant().getId(),
                        matchPlayer.getParticipant().getGameId(),
                        matchPlayer.getParticipant().getGameTier(),
                        matchPlayer.getPlayerStatus(),
                        matchPlayer.getPlayerScore(),
                        matchPlayer.getMatchPlayerResultStatus(),
                        matchPlayer.getParticipant().getProfileImageUrl(),
                        matchPlayer.getPlayerScore()
                ))
                .sorted(Comparator.comparingInt(MatchPlayerInfo::getScore).reversed()
                        .thenComparing(MatchPlayerInfo::getGameId))
                .collect(Collectors.toList());

        assignRankToMatchPlayerInfoList(matchPlayerInfoList);

        return matchPlayerInfoList;
    }

    private Participant checkHost(String channelLink) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);
        checkRoleHost(participant.getRole());

        return participant;
    }

    private void checkUpdateScore(List<Match> matchList) {
        for (Match currentMatch : matchList) {
            List<MatchPlayer> matchplayerList = matchPlayerRepository.findAllByMatch_IdOrderByPlayerScoreDesc(currentMatch.getId());

            int progressCount = 0;

            for (MatchPlayer matchPlayer : matchplayerList) {
                if (progressCount >= 5) {
                    if (!matchPlayer.getParticipant().getParticipantStatus().equals(DISQUALIFICATION)) {
                        matchPlayer.getParticipant().dropoutParticipantStatus();
                    }
                    continue;
                }

                if (matchPlayer.getParticipant().getParticipantStatus().equals(PROGRESS)) {
                    progressCount++;
                } else {
                    matchPlayer.getParticipant().dropoutParticipantStatus();
                }
            }
        }
    }

    private void checkPreviousMatchEnd(String channelLink, Integer matchRound) {
        if(matchRound != 1){
            List<Match> previousMatch = findMatchList(channelLink, matchRound - 1);
            previousMatch.stream()
                    .filter(match -> !match.getMatchStatus().equals(END))
                    .findAny()
                    .ifPresent(match -> { throw new MatchNotFoundException(); });
        }
    }


    public MatchScoreInfoDto getMatchScoreInfo(String channelLink, Long matchId) {
        List<MatchPlayer> matchPlayers = Optional.ofNullable(
                        matchPlayerRepository.findMatchPlayersAndMatchAndParticipantByMatchId(matchId))
                .filter(list -> !list.isEmpty())
                .orElseThrow(MatchNotFoundException::new);

        Match match = matchRepository.findById(matchId)
                .orElseThrow(MatchNotFoundException::new);

        List<MatchPlayerInfo> matchPlayerInfoList = convertMatchPlayerInfoList(matchPlayers);

        Long requestMatchPlayerId = getRequestMatchPlayerId(channelLink, matchPlayers);

        return MatchScoreInfoDto.builder()
                .matchPlayerInfos(matchPlayerInfoList)
                .matchRound(match.getMatchRound())
                .matchCurrentSet(match.getMatchCurrentSet())
                .matchSetCount(match.getMatchSetCount())
                .requestMatchPlayerId(requestMatchPlayerId)
                .build();
    }

    private void assignRankToMatchPlayerInfoList(List<MatchPlayerInfo> matchPlayerInfoList) {
        int rank = INITIAL_RANK;
        for (int i = 0; i < matchPlayerInfoList.size(); i++) {
            MatchPlayerInfo info = matchPlayerInfoList.get(i);
            if (i > 0 && !info.getScore().equals(matchPlayerInfoList.get(i - 1).getScore())) {
                rank = i + 1;
            }
            info.setMatchRank(rank);
        }
    }

    private Long getRequestMatchPlayerId(String channelLink, List<MatchPlayer> matchPlayers) {
        if (memberService.checkIfMemberIsAnonymous()) {
            return 0L;
        }
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);
        
        if (participant.getRole() == Role.HOST) {
            return -1L;
        }

        return findRequestMatchPlayerId(member, matchPlayers);
    }

    private Long findRequestMatchPlayerId( Member member, List<MatchPlayer> matchPlayers) {
        for (MatchPlayer mp : matchPlayers) {
            if (mp.getParticipant().getMember().getId().equals(member.getId())) {
                return mp.getId();
            }
        }
        return 0L;
    }

    private static void updateMatchSetCount(List<Integer> roundCount, List<Match> findMatchList) {
        int responseIndex = 0;
        for(int i = roundCount.size(); i >= 1; i--){
            for(Match match : findMatchList){
                if(match.getMatchRound().equals(i))
                    match.updateMatchSetCount(roundCount.get(responseIndex));
            }
            responseIndex++;
        }

    }

    private static void checkChannelProceeding(Participant participant) {
        if(participant.getChannel().getChannelStatus().equals(PROCEEDING))
            throw new ChannelStatusAlreadyException();
    }

    private void createMatchSet(List<Match> matchList) {
        matchList.stream()
                .flatMap(match -> IntStream.rangeClosed(1, match.getMatchSetCount())
                        .mapToObj(setCount -> MatchSet.createMatchSet(match, setCount)))
                .forEach(matchSetRepository::save);
    }

    private static List<Integer> getMatchSetCountList(List<Match> matchList) {
        List<Integer> matchSetCountList = new ArrayList<>();
        int matchRound = 0;
        for(Match match : matchList){
            if(matchRound == match.getMatchRound())
                continue;
            else {
                matchSetCountList.add(match.getMatchSetCount());
                matchRound = match.getMatchRound();
            }
        }
        return matchSetCountList;
    }

}