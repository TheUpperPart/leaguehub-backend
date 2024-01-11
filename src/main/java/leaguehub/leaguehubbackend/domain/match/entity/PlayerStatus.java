package leaguehub.leaguehubbackend.domain.match.entity;

public enum PlayerStatus {
    READY(1), WAITING(0), DISQUALIFICATION(2);

    private final int status;

    PlayerStatus(int status) { this.status = status; }

    public int getStatus() { return status; }
}
