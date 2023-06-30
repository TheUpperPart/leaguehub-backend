package leaguehub.leaguehubbackend.entity.participant;

public enum Role {
    HOST(0), MANAGER(1), PLAYER(2), OBSERVER(3);

    private int num;

    Role(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
