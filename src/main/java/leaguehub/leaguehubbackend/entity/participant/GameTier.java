package leaguehub.leaguehubbackend.entity.participant;

import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.Arrays;

@Getter
public enum GameTier {

    UNRANKED(-1),
    IRON_IV(0), IRON_III(100), IRON_II(200), IRON_I(300),
    BRONZE_IV(400), BRONZE_III(500), BRONZE_II(600), BRONZE_I(700),
    SILVER_IV(800), SILVER_III(900), SILVER_II(1000), SILVER_I(1100),
    GOLD_IV(1200), GOLD_III(1300), GOLD_II(1400), GOLD_I(1500),
    PLATINUM_IV(1600), PLATINUM_III(1700), PLATINUM_II(1800), PLATINUM_I(1900),
    DIAMOND_IV(2000), DIAMOND_III(2100), DIAMOND_II(2200), DIAMOND_I(2300),
    MASTER_I(2400),
    GRANDMASTER_I(2800),
    CHALLENGER_I(3200);


    private final int score;

    GameTier(int score){
        this.score = score;
    }


    /**
     * rank와 grade를 받아 맞는 티어를 반환
     * @param rank
     * @param grade
     * @return
     */
    public static GameTier findGameTier(String rank, String grade){

        String tier = MessageFormat.format("{0}_{1}", rank, grade);

        for(GameTier gameTier : GameTier.values())
            if(gameTier.name().equalsIgnoreCase(tier))
                return gameTier;

        throw new ParticipantGameIdNotFoundException();
    }

    /**
     * 언랭일 경우 반환
     * @return gameTierDto
     */
    public static GameTier getUnranked(){

        return GameTier.UNRANKED;
    }


    public static GameTier getByNumber(int tier) {
        return Arrays.stream(GameTier.values())
                .filter(gameTier -> gameTier.score == tier)
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }

}
