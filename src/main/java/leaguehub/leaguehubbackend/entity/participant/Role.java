package leaguehub.leaguehubbackend.entity.participant;

public enum Role {
    HOST(0), PLAYER(1), OBSERVER(2);

    private final int num;

    Role(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
