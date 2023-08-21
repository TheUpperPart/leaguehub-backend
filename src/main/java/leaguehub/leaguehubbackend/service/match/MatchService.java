package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.MatchInfoDto;
import leaguehub.leaguehubbackend.dto.match.MatchPlayerInfo;
import leaguehub.leaguehubbackend.dto.match.MatchRoundListDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        while (currentPlayers >= MIN_PLAYERS_FOR_SUB_MATCH) {
            currentPlayers = createSubMatchesForRound(channel, currentPlayers);
        }
    }

    /**
     * 해당 채널의 매치 라운드를 보여줌(64, 32, 16, 8)
     *
     * @param channelLink
     * @return
     */
    public MatchRoundListDto getRoundList(String channelLink) {
        Channel findChannel = getChannel(channelLink);

        int maxPlayers = findChannel.getMaxPlayer();
        List<Integer> roundList = calculateRoundList(maxPlayers);

        MatchRoundListDto roundListDto = new MatchRoundListDto();
        roundListDto.setRoundList(roundList);

        return roundListDto;
    }

    /**
     * 경기 첫 배정
     * @param channelLink
     * @param matchRound
     */
    public void matchAssignment(String channelLink, Integer matchRound) {
        Channel findChannel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(() -> new ChannelNotFoundException());

        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(findChannel.getId(), member.getId());
        checkRoleHost(participant.getRole());

        List<Match> matchList = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channelLink, matchRound);

        List<Participant> playerList = getParticipantList(channelLink, matchRound);

        assignSubMatches(findChannel, matchList, playerList);
    }


    public List<MatchInfoDto> loadMatchPlayerList(String channelLink, Integer matchRound) {
        List<Match> matchList = matchRepository.findAllByChannel_ChannelLinkAndMatchRoundOrderByMatchName(channelLink, matchRound);

        List<MatchInfoDto> matchInfoDtoList = matchList.stream()
                .map(this::createMatchInfoDto)
                .collect(Collectors.toList());

        return matchInfoDtoList;
    }

    private Channel getChannel(String channelLink) {
        Channel findChannel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(() -> new ChannelNotFoundException());
        return findChannel;
    }

    private List<Integer> calculateRoundList(int maxPlayers) {
        List<Integer> roundList = new ArrayList<>();

        while (maxPlayers >= MIN_PLAYERS_FOR_SUB_MATCH) {
            roundList.add(maxPlayers);
            maxPlayers /= 2;
        }

        return roundList;
    }

    private int createSubMatchesForRound(Channel channel, int maxPlayers) {
        int currentPlayers = maxPlayers;
        int tableCount = currentPlayers / MIN_PLAYERS_FOR_SUB_MATCH;

        for (int tableIndex = 1; tableIndex <= tableCount; tableIndex++) {
            String groupName = "Group " + (char) (64 + tableIndex);
            Match match = Match.createMatch(currentPlayers, channel, groupName);
            matchRepository.save(match);
        }

        return currentPlayers / 2;
    }

    private Participant getParticipant(Long channelId, Long memberId) {
        Participant participant = participantRepository.findParticipantByMemberIdAndChannel_Id(memberId, channelId)
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
        List<MatchPlayerInfo> matchPlayerInfoList = new ArrayList<>();

        for (MatchPlayer matchPlayer : playerList) {
            MatchPlayerInfo matchPlayerInfo = new MatchPlayerInfo();
            matchPlayerInfo.setNickName(matchPlayer.getParticipant().getNickname());
            matchPlayerInfo.setGameTier(matchPlayer.getParticipant().getGameTier());
            matchPlayerInfo.setPlayerStatus(matchPlayer.getPlayerStatus());
            matchPlayerInfo.setScore(matchPlayer.getPlayerScore());
            matchPlayerInfoList.add(matchPlayerInfo);
        }

        return matchPlayerInfoList;
    }
}
