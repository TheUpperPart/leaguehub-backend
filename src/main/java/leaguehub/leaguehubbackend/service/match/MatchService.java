package leaguehub.leaguehubbackend.service.match;

import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.match.MatchStatus;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.match.exception.MatchNotEnoughPlayerException;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static leaguehub.leaguehubbackend.entity.participant.RequestStatus.DONE;
import static leaguehub.leaguehubbackend.entity.participant.Role.PLAYER;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final ChannelRepository channelRepository;
    private final ParticipantRepository participantRepository;
    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ChannelService channelService;
    private final MemberService memberService;

    public void matchAssignment(String channelLink) {
        Member member = memberService.findCurrentMember();
        Channel findChannel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(() -> new ChannelNotFoundException());

        Participant participant = channelService.getParticipant(findChannel.getId(), member.getId());
        channelService.checkRoleHost(participant.getRole());

        List<Participant> playerList = participantRepository.findAllByChannel_ChannelLinkAndRoleAndRequestStatusOrderByNicknameAsc(channelLink, PLAYER, DONE);


        if (playerList.size() < findChannel.getMaxPlayer() * 0.75) throw new MatchNotEnoughPlayerException();


        createSubMatch(findChannel, playerList);
    }

    private void createSubMatch(Channel findChannel, List<Participant> playerList) {
        Collections.shuffle(playerList);

        int maxPlayer = findChannel.getMaxPlayer();
        int tableCount = findChannel.getMaxPlayer() / 8;
        int playerCount = findChannel.getRealPlayer() / tableCount;
        int remainingPlayer = findChannel.getRealPlayer() % tableCount;

        int index = 0;


        for (int tableIndex = 1; tableIndex <= tableCount; tableIndex++) {
            Match match = Match.createMatch(maxPlayer, findChannel, "Group " + (char)(64 + tableIndex));
            matchRepository.save(match);

            for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
                MatchPlayer matchPlayer = MatchPlayer.createMatchPlayer(playerList.get(index), match);
                matchPlayerRepository.save(matchPlayer);
                index++;
            }

            if (remainingPlayer > 0) {
                index++;
                remainingPlayer--;
            }
        }
    }
}
