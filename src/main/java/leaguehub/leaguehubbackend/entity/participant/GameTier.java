package leaguehub.leaguehubbackend.entity.participant;

import leaguehub.leaguehubbackend.dto.participant.GameRankDto;
import lombok.Getter;

@Getter
public enum GameTier {

    UNRANKED(-1), NONE(0),
    IRON(0), BRONZE(400), SILVER(800),
    GOLD(1200), PLATINUM(1600), DIAMOND(2000),
    MASTER(2400), GRANDMASTER(2400), CHALLENGER(2400),
    IV(0), III(100), II(200), I(300);


    private int score;

    GameTier(int score){
        this.score = score;
    }


    /**
     * rank와 grade를 받아 맞는 티어를 반환
     * @param rank
     * @param grade
     * @return
     */
    public static GameRankDto findGameTier(String rank, String grade){

        GameRankDto gameTierDto = new GameRankDto();

        for(GameTier gameTier : GameTier.values()){
            if(gameTier.toString().equalsIgnoreCase(rank))
                gameTierDto.setGameRank(gameTier);
            if(gameTier.toString().equalsIgnoreCase(grade))
                gameTierDto.setGameGrade(gameTier.toString());
        }

        return gameTierDto;
    }

    /**
     * 언랭일 경우 반환
     * @return gameTierDto
     */
    public static GameRankDto getUnranked(){
        GameRankDto gameTierDto = new GameRankDto();

        gameTierDto.setGameRank(UNRANKED);
        gameTierDto.setGameGrade("");

        return gameTierDto;
    }

    /**
     * 마스터 이상일 경우 grade 대신 leaguePoints를 반환
     * @param rank
     * @param leaguePoints
     * @return
     */
    public static GameRankDto getRanked(String rank, String leaguePoints) {
        GameRankDto gameTierDto = new GameRankDto();

        for (GameTier gameTier : GameTier.values()) {
            if (gameTier.toString().equalsIgnoreCase(rank))
                gameTierDto.setGameRank(gameTier);

            gameTierDto.setGameGrade(leaguePoints);
        }

        return gameTierDto;
    }

}
