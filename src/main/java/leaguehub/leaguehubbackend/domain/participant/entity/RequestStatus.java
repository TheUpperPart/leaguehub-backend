package leaguehub.leaguehubbackend.domain.participant.entity;

public enum RequestStatus {

    NO_REQUEST(0), REQUEST(1), DONE(2), REJECT(3);

    private final int num;

    RequestStatus(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
