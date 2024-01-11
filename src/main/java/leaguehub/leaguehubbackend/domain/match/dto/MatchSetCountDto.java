package leaguehub.leaguehubbackend.domain.match.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MatchSetCountDto {

    private List<Integer> matchSetCountList;
}
