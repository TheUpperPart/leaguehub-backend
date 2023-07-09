package leaguehub.leaguehubbackend.dto.channel;

import lombok.Data;

@Data
public class ResponseBoardDetail {

    String detail;

    public ResponseBoardDetail(String detail) {
        this.detail = detail;
    }
}
