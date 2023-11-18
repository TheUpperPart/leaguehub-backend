package leaguehub.leaguehubbackend.dto.channel;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelInfoDto {

    @NotBlank
    @Schema(description = "해당 채널의 참가조건", example = "브론즈 이상 마스터 이하")
    private String channelRuleInfo;

    @NotBlank
    @Schema(description = "해당 채널의 대회 시간", example = "해당 대회는 2023-11-18 오후 9시부터 시작입니다.")
    private String channelTimeInfo;

    @NotBlank
    @Schema(description = "해당 채널의 상품 ", example = "1등 1,000원 2등 100원 3등 10원 4등 1원 5등 꽝")
    private String channelPrizeInfo;

    public ChannelInfoDto(String channelRuleInfo, String channelTimeInfo, String channelPrizeInfo){
        this.channelRuleInfo = channelRuleInfo;
        this.channelTimeInfo = channelTimeInfo;
        this.channelPrizeInfo = channelPrizeInfo;
    }
}
