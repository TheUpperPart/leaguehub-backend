package leaguehub.leaguehubbackend.dto.match;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MatchSetCountDto {

    private List<Integer> matchSetCountList;
}
