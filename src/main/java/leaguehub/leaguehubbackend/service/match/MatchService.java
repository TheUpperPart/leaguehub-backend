package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.dto.match.MatchRoundListDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ChannelRepository channelRepository;
    private final ParticipantRepository participantRepository;

    private static final int MIN_PLAYERS_FOR_SUB_MATCH = 8;

    /**
     * 채널을 만들 때 빈 값인 매치를 만듦
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
     * @param channelLink
     * @return
     */
    public MatchRoundListDto getRoundList(String channelLink) {
        Channel channel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(() -> new ChannelNotFoundException());

        int maxPlayers = channel.getMaxPlayer();
        List<Integer> roundList = calculateRoundList(maxPlayers);

        MatchRoundListDto roundListDto = new MatchRoundListDto();
        roundListDto.setRoundList(roundList);

        return roundListDto;
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

    private List<Integer> calculateRoundList(int maxPlayers) {
        List<Integer> roundList = new ArrayList<>();

        while (maxPlayers >= MIN_PLAYERS_FOR_SUB_MATCH) {
            roundList.add(maxPlayers);
            maxPlayers /= 2;
        }

        return roundList;
    }
}
