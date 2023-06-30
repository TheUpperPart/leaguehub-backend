package leaguehub.leaguehubbackend.entity.participant;

import lombok.Getter;

@Getter
public enum GameTier {

    UNRANKED(-1, "랭크없음"),
    IRON(0, "아이언"), BRONZE(400, "브론즈"), SILVER(800, "실버"),
    GOLD(1200, "골드"), PLATINUM(1600, "플래티넘"), DIAMOND(2000, "다이아몬드"),
    MASTER(2400, "마스터"), GRANDMASTER(2400, "그랜드 마스터"), CHALLENGER(2400, "챌린저"),
    IV(0, "4"), III(100, "3"), II(200, "2"), I(300, "1");


    private int score;
    private String tier;

    GameTier(int score, String tier){
        this.score = score;
        this.tier = tier;
    }



    //
    public static String findGameTier(String rank, String grade){

        String resultTier = "";
        String resultGrade = "";

        for(GameTier gameTier : GameTier.values()){
            if(gameTier.toString().equalsIgnoreCase(rank))
                resultTier = gameTier.tier;
            if(gameTier.toString().equalsIgnoreCase(grade))
                resultGrade = gameTier.tier;
        }

        return resultTier.concat(" ").concat(resultGrade);
    }

}
