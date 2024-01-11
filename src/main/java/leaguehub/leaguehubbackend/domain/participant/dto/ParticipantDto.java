package leaguehub.leaguehubbackend.domain.participant.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParticipantDto {


    @NotBlank
    @Schema(description = "참가하려는 게임 닉네임과 태그", example = "칸영기#KR1")
    private String gameId;

    @NotBlank
    @Schema(description = "채널 닉네임", example = "채널개인닉네임")
    private String nickname;
}
