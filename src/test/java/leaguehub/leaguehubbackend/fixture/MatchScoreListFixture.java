package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.domain.match.dto.MatchPlayerInfo;

import java.util.Arrays;
import java.util.List;

public class MatchScoreListFixture {
    public static List<MatchPlayerInfo> createExpectedList() {
        return Arrays.asList(
                MatchPlayerInfo.builder()
                        .matchPlayerId(1L)
                        .participantId(1L)
                        .matchRank(1)
                        .profileSrc("url")
                        .gameId("NO_DATA")
                        .score(8)
                        .build(),
                MatchPlayerInfo.builder()
                        .matchPlayerId(2L)
                        .participantId(2L)
                        .matchRank(2)
                        .profileSrc("url")
                        .gameId("NO_DATA")
                        .score(7)
                        .build(),
                MatchPlayerInfo.builder()
                        .matchPlayerId(3L)
                        .participantId(3L)
                        .matchRank(2)
                        .profileSrc("url")
                        .gameId("NO_DATA")
                        .score(7)
                        .build(),
                MatchPlayerInfo.builder()
                        .matchPlayerId(4L)
                        .participantId(4L)
                        .matchRank(4)
                        .profileSrc("url")
                        .gameId("NO_DATA")
                        .score(6)
                        .build()
        );
    }
}

