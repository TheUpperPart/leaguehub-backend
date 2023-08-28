package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.MatchInfoDto;
import leaguehub.leaguehubbackend.dto.match.MatchPlayerInfo;
import leaguehub.leaguehubbackend.dto.match.MatchRoundInfoDto;
import leaguehub.leaguehubbackend.dto.match.MatchRoundListDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.match.MatchStatus;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotEnoughPlayerException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static leaguehub.leaguehubbackend.entity.constant.GlobalConstant.NO_DATA;
import static leaguehub.leaguehubbackend.entity.participant.RequestStatus.DONE;
import static leaguehub.leaguehubbackend.entity.participant.Role.PLAYER;

@Service
@RequiredArgsConstructor
public class MatchService {

    private static final int MIN_PLAYERS_FOR_SUB_MATCH = 8;
    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ChannelRepository channelRepository;
    private final ParticipantRepository participantRepository;
    private final MemberService memberService;


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
     * 경기 첫 배정
     *
     * @param channelLink
     * @param matchRound
     */
    public void matchAssignment(String channelLink, Integer matchRound) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);
        Channel channel = participant.getChannel();
        checkRoleHost(participant.getRole());

        List<Match> matchList = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channelLink, matchRound);

        List<Participant> playerList = getParticipantList(channelLink, matchRound);

        assignSubMatches(channel, matchList, playerList);
    }


    public MatchRoundInfoDto loadMatchPlayerList(String channelLink, Integer matchRound) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);

        List<Match> matchList = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channelLink, matchRound);

        List<MatchInfoDto> matchInfoDtoList = matchList.stream()
                .map(this::createMatchInfoDto)
                .collect(Collectors.toList());

        MatchRoundInfoDto matchRoundInfoDto = new MatchRoundInfoDto();

        findMyRoundName(participant, matchList, matchRoundInfoDto);

        matchRoundInfoDto.setMatchInfoDtoList(matchInfoDtoList);
        return matchRoundInfoDto;
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
                    List<Match> matchList = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channelLink, round);
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

    private List<Participant> getParticipantList(String channelLink, Integer matchRound) {
        List<Participant> playerList = participantRepository
                .findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, PLAYER, DONE);

        if (playerList.size() < matchRound * 0.75) throw new MatchNotEnoughPlayerException();
        return playerList;
    }

    private void assignSubMatches(Channel channel, List<Match> matchList, List<Participant> playerList) {
        Collections.shuffle(playerList);

        int totalPlayers = channel.getRealPlayer();
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
        matchInfoDto.setMatchLink(match.getMatchLink());
        matchInfoDto.setMatchStatus(match.getMatchStatus());
        matchInfoDto.setMatchRound(match.getMatchRound());
        matchInfoDto.setMatchRoundCount(match.getRoundRealCount());
        matchInfoDto.setMatchRoundMaxCount(match.getRoundMaxCount());

        List<MatchPlayer> playerList = matchPlayerRepository.findAllByMatch_Id(match.getId());
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
                    return matchPlayerInfo;
                })
                .collect(Collectors.toList());

        return matchPlayerInfoList;

    }

    private void findMyRoundName(Participant participant, List<Match> matchList, MatchRoundInfoDto matchRoundInfoDto) {
        matchRoundInfoDto.setMyGameId(NO_DATA.getData());

        if (!participant.getGameId().equalsIgnoreCase(NO_DATA.getData())) {
            matchList.forEach(match -> {
                List<MatchPlayer> playerList = matchPlayerRepository.findAllByMatch_Id(match.getId());
                playerList.stream()
                        .filter(player -> participant.getGameId().equalsIgnoreCase(player.getParticipant().getGameId()))
                        .findFirst()
                        .ifPresent(player -> matchRoundInfoDto.setMyGameId(participant.getGameId()));
            });
        }
    }
}
