package leaguehub.leaguehubbackend.service.participant;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ParticipantServiceTest {

    @Autowired
    ParticipantService participantService;

    @Test
    @DisplayName("티어 검색 성공 테스트")
    void getTierSuccessTest() throws Exception {

        String tier1 = participantService.getTier("칸영기");
        String tier2 = participantService.getTier("서초임");
        String tier3 = participantService.getTier("채수채수밭");
        String tier4 = participantService.getTier("사라진검 리븐");

        assertThat(tier1).isEqualToIgnoringCase("platinum iv");
        assertThat(tier2).isEqualToIgnoringCase("unranked");
        assertThat(tier3).isEqualToIgnoringCase("challenger i");
        assertThat(tier4).isEqualToIgnoringCase("grandmaster i");
    }

    @Test
    @DisplayName("티어 검색 실패 테스트")
    void getTierFailTest() throws Exception {

        assertThatThrownBy(() -> participantService.getTier("saovkovsk"))
                .isInstanceOf(ParticipantGameIdNotFoundException.class);
    }
}