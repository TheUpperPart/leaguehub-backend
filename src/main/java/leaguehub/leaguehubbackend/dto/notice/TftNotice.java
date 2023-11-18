package leaguehub.leaguehubbackend.dto.notice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TftNotice {

    private String noticeLink;

    private String noticeTitle;

    private String noticeInfo;
}
