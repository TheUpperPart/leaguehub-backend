package leaguehub.leaguehubbackend.entity.channel;

public enum MatchFormat {
    SINGLE_ELIMINATION(1), FREE_FOR_ALL(0);

    private int num;

    MatchFormat(int num) {
        this.num = num;
    }

    public static MatchFormat getByNumber(int number) {
        for (MatchFormat matchFormat : MatchFormat.values()) {
            if (matchFormat.num == number) {
                return matchFormat;
            }
        }
        return null; // 해당하는 값이 없을 경우 null 반환
    }
}
