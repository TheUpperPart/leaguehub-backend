package leaguehub.leaguehubbackend.dto.participant;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ParticipantResponseDto {

    @Schema(description = "조회하는 매치 링크", example = "42aa1b11ab88")
    String channelLink;

    @Schema(description = "참가하려는 게임 닉네임", example = "칸영기")
    String gameId;

    @Schema(description = "채널 닉네임", example = "채널개인닉네임")
    String nickname;
}
