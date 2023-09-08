package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.match.MatchPlayerScoreInfo;

import java.util.Arrays;
import java.util.List;

public class MatchScoreListFixture {
    public static List<MatchPlayerScoreInfo> createExpectedList() {
        return Arrays.asList(
                MatchPlayerScoreInfo.builder()
                        .matchPlayerId(1L)
                        .participantId(1L)
                        .matchRank(1)
                        .participantImageUrl("url")
                        .participantGameId("NO_DATA")
                        .playerScore(8)
                        .build(),
                MatchPlayerScoreInfo.builder()
                        .matchPlayerId(2L)
                        .participantId(2L)
                        .matchRank(2)
                        .participantImageUrl("url")
                        .participantGameId("NO_DATA")
                        .playerScore(7)
                        .build(),
                MatchPlayerScoreInfo.builder()
                        .matchPlayerId(3L)
                        .participantId(3L)
                        .matchRank(2)
                        .participantImageUrl("url")
                        .participantGameId("NO_DATA")
                        .playerScore(7)
                        .build(),
                MatchPlayerScoreInfo.builder()
                        .matchPlayerId(4L)
                        .participantId(4L)
                        .matchRank(4)
                        .participantImageUrl("url")
                        .participantGameId("NO_DATA")
                        .playerScore(6)
                        .build()
        );
    }
}
