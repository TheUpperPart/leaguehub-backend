package leaguehub.leaguehubbackend.entity.participant;

public enum Request {

    NOREQUEST(0), REQUEST(1), DONE(2), REJECT(3);

    private int num;

    Request(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
