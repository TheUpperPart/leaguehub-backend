package leaguehub.leaguehubbackend.entity.participant;

import lombok.Getter;

@Getter
public enum GameTier {

    UNRANKED(-1),
    IRON(0), BRONZE(400), SILVER(800),
    GOLD(1200), PLATINUM(1600), DIAMOND(2000),
    MASTER(2400), GRANDMASTER(2400), CHALLENGER(2400),
    IV(0), III(100), II(200), I(300);


    private int score;

    GameTier(int score){
        this.score = score;
    }



    //
    public static String findGameTier(String rank, String grade){

        String resultTier = "";
        String resultGrade = "";

        for(GameTier gameTier : GameTier.values()){
            if(gameTier.toString().equalsIgnoreCase(rank))
                resultTier = gameTier.toString();
            if(gameTier.toString().equalsIgnoreCase(grade))
                resultGrade = gameTier.toString();
        }

        return resultTier.concat(" ").concat(resultGrade);
    }

}
