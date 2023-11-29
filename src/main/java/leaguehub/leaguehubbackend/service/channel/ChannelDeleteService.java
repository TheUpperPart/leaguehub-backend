package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.entity.match.MatchPlayer;
import leaguehub.leaguehubbackend.entity.match.MatchSet;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelStatusAlreadyException;
import leaguehub.leaguehubbackend.exception.participant.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantNotGameHostException;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelInfoRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.match.MatchPlayerRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.repository.match.MatchSetRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static leaguehub.leaguehubbackend.entity.channel.ChannelStatus.PREPARING;
import static leaguehub.leaguehubbackend.entity.participant.Role.HOST;

@RequiredArgsConstructor
@Transactional
@Service
public class ChannelDeleteService {

    private final ChannelRepository channelRepository;
    private final MemberService memberService;
    private final ChannelBoardRepository channelBoardRepository;
    private final ParticipantRepository participantRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ChannelRuleRepository channelRuleRepository;
    private final MatchSetRepository matchSetRepository;
    private final ChannelInfoRepository channelInfoRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public void deleteChannel(String channelLink) {
        Member member = memberService.findCurrentMember();
        Participant participant = getParticipant(member.getId(), channelLink);
        Channel channel = getChannel(channelLink);

        if (channel.getChannelStatus() != PREPARING) {
            throw new ChannelStatusAlreadyException();
        }

        if (participant.getRole() != HOST) {
            throw new ParticipantNotGameHostException();
        }

        deleteParticipant(channelLink);
        deleteMatch(channelLink);

        deleteChannelBoards(channelLink);

        deleteChannelInfo(channelLink);

        deleteChannelRule(channelLink);

        channelRepository.delete(channel);
        channelRepository.flush();
    }

    private void deleteChannelBoards(String channelLink) {
        List<ChannelBoard> channelBoards = channelBoardRepository.findChannelBoardsByChannel_ChannelLink(channelLink);
        channelBoards.stream().forEach(channelBoard -> {
            channelBoard.deleteChannel();
        });

        channelBoardRepository.deleteAllInBatch(channelBoards);
        channelBoardRepository.flush();
    }

    private void deleteChannelInfo(String channelLink) {
        channelInfoRepository.findChannelInfoByChannel_ChannelLink(channelLink).ifPresent(
                channelInfo -> {
                    channelInfo.deleteChannel();
                    channelInfoRepository.delete(channelInfo);
                }
        );

        channelInfoRepository.flush();
    }

    private void deleteChannelRule(String channelLink) {
        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(channelLink);
        channelRule.deleteChannel();
        channelRuleRepository.delete(channelRule);
        channelRuleRepository.flush();
    }

    private void deleteMatch(String channelLink) {
        List<Match> matchList = matchRepository.findAllByChannel_ChannelLink(channelLink);

        matchList.stream().forEach(match -> {
            match.deleteChannel();
            List<MatchSet> matchSetList = matchSetRepository.findAllByMatch_Channel_ChannelLink(channelLink);
            matchSetList.stream().forEach(matchSet -> {
                matchSet.getMatchRankList().stream().forEach(matchRank -> {
                    matchRank.deleteMatchSet();
                });
                matchSet.deleteMatchAndMatchRankList();
            });

            matchSetRepository.deleteAllInBatch(matchSetList);
        });

        matchRepository.deleteAllInBatch(matchList);
    }

    private void deleteParticipant(String channelLink) {
        List<Participant> participants = participantRepository.findAllByChannel_ChannelLink(channelLink);

        participants.stream().forEach(p -> {
            p.deleteChannelAndMember();
            List<MatchPlayer> matchPlayers = matchPlayerRepository.findMatchPlayersByParticipantId(p.getId());
            matchPlayers.stream().forEach(mp -> {
                mp.deleteParticipantAndMatch();
            });
            matchPlayerRepository.deleteAllInBatch(matchPlayers);
        });

        participantRepository.deleteAllInBatch(participants);
    }

    private Channel getChannel(String channelLink) {
        Channel channel = channelRepository.findByChannelLink(channelLink)
                .orElseThrow(ChannelNotFoundException::new);
        return channel;
    }


    private Participant getParticipant(Long memberId, String channelLink) {
        return participantRepository
                .findParticipantByMemberIdAndChannel_ChannelLink(memberId, channelLink)
                .orElseThrow(() -> new InvalidParticipantAuthException());
    }
}
