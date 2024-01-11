package leaguehub.leaguehubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.domain.match.repository.MatchRepository;
import leaguehub.leaguehubbackend.domain.match.service.MatchPlayerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class MatchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MatchPlayerService matchPlayerService;

    @Autowired
    ObjectMapper mapper;


    @Test
    @DisplayName("경기 결과 생성 테스트 - 성공")
    public void createMatchRankSuccessTest() throws Exception {


    }

    @Test
    @DisplayName("경기 결과 생성 테스트 - 실패")
    public void createMatchRankFailTest() throws Exception {

    }

}
