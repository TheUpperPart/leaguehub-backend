package leaguehub.leaguehubbackend.service.participant;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.entity.participant.GameTier;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ParticipantServiceTest {

    @Autowired
    ParticipantService participantService;

    @Test
    @DisplayName("티어, 플레이 횟수 검색 성공 테스트")
    void getDetailSuccessTest() throws Exception {

        ResponseUserDetailDto testDto1 = participantService.getTierAndPlayCount("칸영기");
        ResponseUserDetailDto testDto2 = participantService.getTierAndPlayCount("서초임");
        ResponseUserDetailDto testDto3 = participantService.getTierAndPlayCount("채수채수밭");

        assertThat(testDto1.getTier()).isEqualTo("DIAMOND IV");
        assertThat(testDto2.getTier()).isEqualTo("UNRANKED");

        assertThat(testDto2.getPlayCount()).isEqualTo(0);

        System.out.println(testDto3.getTier());
    }

    @Test
    @DisplayName("티어, 플레이 횟수 검색 실패 테스트")
    void getDetailFailTest() throws Exception {

        assertThatThrownBy(() -> participantService.getTierAndPlayCount("saovkovsk"))
                .isInstanceOf(ParticipantGameIdNotFoundException.class);
    }


}