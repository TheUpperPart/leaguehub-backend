package leaguehub.leaguehubbackend.dto.participant;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ParticipantResponseDto {

    @Schema(example = "42aa1b11ab88")
    String channelLink;

    @Schema(example = "칸영기")
    String gameId;

    @Schema(example = "채널개인닉네임")
    String nickname;
}
