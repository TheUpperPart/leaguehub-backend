package leaguehub.leaguehubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.match.MatchResponseDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.match.Match;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.match.MatchRepository;
import leaguehub.leaguehubbackend.service.match.MatchRankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    MatchRankService matchRankService;

    @Autowired
    ObjectMapper mapper;


    @Test
    @DisplayName("경기 결과 생성 테스트 - 성공")
    public void createMatchRankSuccessTest() throws Exception{
        //given
        Channel channel = ChannelFixture.createDummyChannel(false, false, "Silver", "iv", 100);
        channelRepository.save(channel);
        Match match = Match.createMatch(16, channel);
        matchRepository.save(match);

        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchLink(match.getMatchLink());
        matchResponseDto.setNickName("서초임");
        String dtoToJson = mapper.writeValueAsString(matchResponseDto);
        //when
        mockMvc.perform(post("/api/match/matchResult")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoToJson))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("경기 결과 생성 테스트 - 실패")
    public void createMatchRankFailTest() throws Exception{
        //given
        Channel channel = ChannelFixture.createDummyChannel(false, false, "Silver", "iv", 100);
        channelRepository.save(channel);
        Match match = Match.createMatch(16, channel);
        matchRepository.save(match);

        MatchResponseDto matchResponseDto = new MatchResponseDto();
        matchResponseDto.setMatchLink(match.getMatchLink());
        matchResponseDto.setNickName("savokscmo");
        String dtoToJson = mapper.writeValueAsString(matchResponseDto);
        //when
        mockMvc.perform(post("/api/match/matchResult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isNotFound());
    }

}
