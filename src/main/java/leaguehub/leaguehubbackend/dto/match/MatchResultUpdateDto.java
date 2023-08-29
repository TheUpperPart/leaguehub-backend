package leaguehub.leaguehubbackend.dto.match;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MatchResultUpdateDto {

    @NotBlank
    private Long matchId;

    @NotBlank
    private Long matchPlayerId;

    @NotBlank
    private Integer placement;

}
