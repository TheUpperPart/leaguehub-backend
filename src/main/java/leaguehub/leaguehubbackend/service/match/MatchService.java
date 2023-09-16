package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.*;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.match.MatchStatus;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotEnoughPlayerException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotFoundException;
import leaguehub.leaguehubbackend.exception.member.exception.MemberNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static leaguehub.leaguehubbackend.entity.constant.GlobalConstant.NO_DATA;

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

        List<Match> matchList = findMatchList(channelLink, matchRound);

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

    public Integer getMyMatchRound(String channelLink) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);

        MatchRoundListDto roundListDto = new MatchRoundListDto();
        roundListDto.setLiveRound(0);

        if (participant.getRole().equals(PLAYER)
                && participant.getParticipantStatus().equals(PROGRESS)) {
            int maxPlayers = participant.getChannel().getMaxPlayer();
            List<Integer> roundList = calculateRoundList(maxPlayers);
            roundListDto.setRoundList(roundList);

            findLiveRound(channelLink, roundList, roundListDto);
        }

        return roundListDto.getLiveRound();

    }

    public void setMatchSetCount(String channelLink, List<Integer> roundCount){

        List<Match> findMatchList = matchRepository.findAllByChannel_ChannelLink(channelLink);

        if(findMatchList.isEmpty())
            throw new MatchNotFoundException();

        AtomicInteger roundCountIndex = new AtomicInteger(0);

        updateMatchSetCount(roundCount, findMatchList, roundCountIndex);
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

        List<MatchPlayer> playerList = matchPlayerRepository.findAllByMatch_IdOrderByPlayerScoreDesc(match.getId());
        List<MatchPlayerInfo> matchPlayerInfoList = createMatchPlayerInfoList(playerList);
        matchInfoDto.setMatchPlayerInfoList(matchPlayerInfoList);

        return matchInfoDto;
    }

    private List<MatchPlayerInfo> createMatchPlayerInfoList(List<MatchPlayer> playerList) {
        List<MatchPlayerInfo> matchPlayerInfoList = playerList.stream()
                .map(matchPlayer -> {
                    MatchPlayerInfo matchPlayerInfo = new MatchPlayerInfo();
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
                .build();
    }


    public List<MatchPlayerInfo> convertMatchPlayerInfoList(List<MatchPlayer> matchPlayers) {
        return matchPlayers.stream()
                .map(matchPlayer -> new MatchPlayerInfo(
                        matchPlayer.getParticipant().getGameId(),
                        matchPlayer.getParticipant().getGameTier(),
                        matchPlayer.getPlayerStatus(),
                        matchPlayer.getPlayerScore(),
                        matchPlayer.getMatchPlayerResultStatus()
                ))
                .sorted(Comparator.comparingInt(MatchPlayerInfo::getScore).reversed())
                .collect(Collectors.toList());
    }

    private Participant checkHost(String channelLink) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);
        checkRoleHost(participant.getRole());

        return participant;
    }


    public MatchScoreInfoDto getMatchScoreInfo(Long matchId) {
        Member member = memberService.findCurrentMember();

        List<MatchPlayer> matchPlayers = Optional.ofNullable(
                        matchPlayerRepository.findMatchPlayersAndMatchAndParticipantByMatchId(matchId))
                .filter(list -> !list.isEmpty())
                .orElseThrow(MatchNotFoundException::new);

        List<MatchPlayerScoreInfo> matchPlayerScoreInfoList = convertToMatchPlayerScoreInfoList(matchPlayers);

        sortAndRankMatchPlayerScoreInfoList(matchPlayerScoreInfoList);

        String requestMatchPlayerId = findRequestMatchPlayerId(member, matchPlayers);

        return MatchScoreInfoDto.builder()
                .matchPlayerScoreInfos(matchPlayerScoreInfoList)
                .requestMatchPlayerId(requestMatchPlayerId)
                .build();
    }

    private List<MatchPlayerScoreInfo> convertToMatchPlayerScoreInfoList(List<MatchPlayer> matchPlayers) {
        return matchPlayers.stream()
                .map(mp -> MatchPlayerScoreInfo.builder()
                        .matchPlayerId(mp.getId())
                        .participantId(mp.getParticipant().getId())
                        .participantImageUrl(mp.getParticipant().getProfileImageUrl())
                        .participantGameId(mp.getParticipant().getGameId())
                        .playerScore(mp.getPlayerScore())
                        .build())
                .collect(Collectors.toList());
    }

    private void sortAndRankMatchPlayerScoreInfoList(List<MatchPlayerScoreInfo> matchPlayerScoreInfoList) {
        sortMatchPlayerScoreInfoList(matchPlayerScoreInfoList);
        assignRankToMatchPlayerScoreInfoList(matchPlayerScoreInfoList);
    }

    private void sortMatchPlayerScoreInfoList(List<MatchPlayerScoreInfo> matchPlayerScoreInfoList) {
        matchPlayerScoreInfoList.sort(Comparator
                .comparing(MatchPlayerScoreInfo::getPlayerScore).reversed()
                .thenComparing(MatchPlayerScoreInfo::getParticipantGameId));
    }
    private void assignRankToMatchPlayerScoreInfoList(List<MatchPlayerScoreInfo> matchPlayerScoreInfoList) {
        int rank = INITIAL_RANK;
        for (int i = 0; i < matchPlayerScoreInfoList.size(); i++) {
            MatchPlayerScoreInfo info = matchPlayerScoreInfoList.get(i);
            if (i > 0 && !info.getPlayerScore().equals(matchPlayerScoreInfoList.get(i - 1).getPlayerScore())) {
                rank = i + 1;
            }
            info.setMatchRank(rank);
        }
    }

    private String findRequestMatchPlayerId(Member member, List<MatchPlayer> matchPlayers) {
        for (MatchPlayer mp : matchPlayers) {
            if (mp.getParticipant().getMember().getId().equals(member.getId())) {
                return mp.getId().toString();
            }
        }
        return "Observer";
    }

    private static void updateMatchSetCount(List<Integer> roundCount, List<Match> findMatchList, AtomicInteger roundCountIndex) {
        IntStream.rangeClosed(1, roundCount.size() + 1)
                .forEach(roundIndex -> findMatchList.stream()
                        .filter(match -> match.getMatchRound() == roundIndex)
                        .forEach(match -> {
                            match.updateMatchSetCount(roundCount.get(roundCountIndex.get()));
                            roundCountIndex.incrementAndGet();
                        }));
    }

}