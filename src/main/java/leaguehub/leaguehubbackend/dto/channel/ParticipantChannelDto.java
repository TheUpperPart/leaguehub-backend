package leaguehub.leaguehubbackend.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantChannelDto {

    @Schema(description = "조회하는 매치 링크", example = "42aa1b11ab88")
    private String channelLink;

    @Schema(description = "채널의 제목", example = "42aa1b11ab88")
    private String title;

    @Schema(description = "게임 종목(TFT, LOL, FIFA)의 숫자", example = "0, 1, 2")
    private Integer category;

    @Schema(description = "채널의 이미지 주소", example = "https://s3.[aws-region].amazonaws.com/[bucket name]")
    private String imgSrc;

    @Schema(description = "사이드 바의 채널의 순서 인덱스", example = "0, 1, 2")
    private Integer customChannelIndex;

    public ParticipantChannelDto(String channelLink, String title, Integer category, String imgSrc, Integer customChannelIndex) {
        this.channelLink = channelLink;
        this.title = title;
        this.category = category;
        this.imgSrc = imgSrc;
        this.customChannelIndex = customChannelIndex;
    }
}
